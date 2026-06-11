package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AppProperties;
import com.java.sadna.backend.sportshop.config.ImagesProperties;
import com.java.sadna.backend.sportshop.entity.OrderItemEntity;
import com.java.sadna.backend.sportshop.entity.PaymentEntity;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.InternalServerErrorException;
import com.java.sadna.backend.sportshop.model.CartValidationResultDto;
import com.java.sadna.backend.sportshop.model.CartViewRowDto;
import com.java.sadna.backend.sportshop.model.CheckoutRequestDto;
import com.java.sadna.backend.sportshop.model.CheckoutResultDto;
import com.java.sadna.backend.sportshop.model.ShippingDetailsDto;
import com.java.sadna.backend.sportshop.repository.CartItemRepository;
import com.java.sadna.backend.sportshop.repository.OrderItemRepository;
import com.java.sadna.backend.sportshop.repository.OrderRepository;
import com.java.sadna.backend.sportshop.repository.PaymentRepository;
import com.java.sadna.backend.sportshop.repository.ProductStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private static final String ORDER_STATUS_PAID = "PAID";
    private static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    private static final String PAYMENT_PROVIDER_MOCK_CARD = "MOCK_CARD";
    private static final String CURRENCY_USD = "USD";

    private static final String MSG_VERSION_MISMATCH =
            "Some items in your cart have changed since you started checkout. Close this dialog and click Proceed to Checkout again to review the updates.";
    private static final String MSG_INSUFFICIENT_STOCK_PREFLIGHT =
            "Some items are no longer available in the quantity you requested. Close this dialog and refresh your cart to see the latest availability.";
    private static final String MSG_INSUFFICIENT_STOCK_RACE =
            "A conflict occurred while processing your order. Please try again, or close this dialog and click Proceed to Checkout again to see the latest cart state.";

    private static final int ORDER_NUMBER_RETRY_CAP = 5;
    private static final String ORDER_NUMBER_EXHAUSTED_MESSAGE =
            "Could not generate a unique order number. Please retry your order.";

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final ProductStockRepository productStockRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderNumberGenerator orderNumberGenerator;
    private final MockPaymentProcessor mockPaymentProcessor;
    private final ImagesProperties imagesProperties;

    public CheckoutService(CartService cartService,
                           CartItemRepository cartItemRepository,
                           ProductStockRepository productStockRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           PaymentRepository paymentRepository,
                           OrderNumberGenerator orderNumberGenerator,
                           MockPaymentProcessor mockPaymentProcessor,
                           AppProperties appProperties) {
        this.cartService = cartService;
        this.cartItemRepository = cartItemRepository;
        this.productStockRepository = productStockRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.orderNumberGenerator = orderNumberGenerator;
        this.mockPaymentProcessor = mockPaymentProcessor;
        this.imagesProperties = appProperties.getImages();
    }

    @Transactional
    public CheckoutResultDto checkout(Long userId, CheckoutRequestDto request) {
        log.info("Checkout started: userId={}", userId);

        // Empty cart bubbles out as a 409 ConflictException from CartService.validateForCheckout.
        CartValidationResultDto validation = cartService.validateForCheckout(userId);
        log.info("Checkout pre-flight: ok={} versionMismatches={} stockIssues={}",
                validation.isOk(),
                validation.getVersionMismatches().size(),
                validation.getStockIssues().size());
        if (!validation.isOk()) {
            boolean versionDrift = !validation.getVersionMismatches().isEmpty();
            String message = versionDrift ? MSG_VERSION_MISMATCH : MSG_INSUFFICIENT_STOCK_PREFLIGHT;
            log.warn("Checkout pre-flight failed: versionDrift={}", versionDrift);
            throw new ConflictException(message);
        }

        // sort first so parallel checkouts hit the same rows in the same order -- no deadlocks
        List<CartViewRowDto> rows = cartItemRepository.findCartViewRowsByUserId(userId).stream()
                .filter(r -> r.getProductPk() != null)
                .sorted(Comparator
                        .comparing(CartViewRowDto::getProductId)
                        .thenComparing(CartViewRowDto::getSize))
                .toList();

        for (CartViewRowDto row : rows) {
            int requestedQty = row.getQuantity();
            int expectedVersion = row.getProductVersionInCart();
            log.debug("Stock deduct attempt: productId={} size={} qty={}",
                    row.getProductId(), row.getSize(), requestedQty);
            int affected = productStockRepository.deductIfAvailable(
                    row.getProductId(), row.getSize(), requestedQty, expectedVersion);
            log.debug("Stock deduct result: productId={} size={} affected={}",
                    row.getProductId(), row.getSize(), affected);
            if (affected == 0) {
                log.warn("Stock race: productId={} size={} requestedQty={} expectedVersion={}",
                        row.getProductId(), row.getSize(), requestedQty, expectedVersion);
                throw new ConflictException(MSG_INSUFFICIENT_STOCK_RACE);
            }
        }

        BigDecimal totalPrice = rows.stream()
                .map(r -> r.getProductPrice().multiply(BigDecimal.valueOf(r.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int itemCount = rows.size();

        ShippingDetailsDto shipping = request.getShipping();
        String orderNumber = null;
        Long orderId = null;
        for (int attempt = 1; attempt <= ORDER_NUMBER_RETRY_CAP; attempt++) {
            orderNumber = orderNumberGenerator.generate();
            log.debug("Order number attempt: attempt={} number={}", attempt, orderNumber);
            int inserted = orderRepository.insertIfUniqueNumber(
                    userId,
                    ORDER_STATUS_PAID,
                    totalPrice,
                    orderNumber,
                    shipping.getFullName(),
                    shipping.getEmail(),
                    shipping.getPhone(),
                    shipping.getCountry(),
                    shipping.getCity(),
                    shipping.getAddressLine());
            if (inserted > 0) {
                orderId = orderRepository.findIdByOrderNumber(orderNumber).orElseThrow();
                break;
            }
            log.warn("Order number collision: attempt={} number={}", attempt, orderNumber);
        }
        if (orderId == null) {
            log.error("Order number exhausted: attempts={}", ORDER_NUMBER_RETRY_CAP);
            throw new InternalServerErrorException(ORDER_NUMBER_EXHAUSTED_MESSAGE);
        }
        log.info("Order persisted: orderId={} orderNumber={} total={} itemCount={}",
                orderId, orderNumber, totalPrice, itemCount);

        // product_name / product_image_url / size + product_version are snapshotted from the
        // cart row -- the version the user actually agreed to buy.
        List<OrderItemEntity> items = new ArrayList<>(rows.size());
        for (CartViewRowDto row : rows) {
            items.add(new OrderItemEntity(
                    orderId,
                    row.getProductId(),
                    row.getProductVersionInCart(),
                    row.getQuantity(),
                    row.getProductPrice(),
                    row.getProductName(),
                    imagesProperties.getProductImageUrl(row.getProductImageFilename()),
                    row.getSize()
            ));
        }
        orderItemRepository.saveAll(items);
        log.debug("Order items persisted: orderId={} count={}", orderId, items.size());

        // Decline propagates as BadGatewayException -> @Transactional rolls back the order +
        // items rows; GlobalExceptionHandler returns the 502 ApiError envelope.
        String transactionId = mockPaymentProcessor.process(request.getPayment(), totalPrice);

        paymentRepository.save(new PaymentEntity(
                orderId,
                PAYMENT_STATUS_SUCCESS,
                totalPrice,
                CURRENCY_USD,
                PAYMENT_PROVIDER_MOCK_CARD,
                transactionId
        ));
        log.debug("Payment persisted: orderId={}", orderId);

        cartItemRepository.deleteByUserId(userId);
        log.debug("Cart cleared: userId={}", userId);

        log.info("Checkout completed: userId={} orderId={} orderNumber={} total={}",
                userId, orderId, orderNumber, totalPrice);

        return new CheckoutResultDto(orderNumber, itemCount, totalPrice);
    }
}
