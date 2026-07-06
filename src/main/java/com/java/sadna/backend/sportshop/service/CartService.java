package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.entity.ProductStockEntity;
import com.java.sadna.backend.sportshop.entity.id.CartItemId;
import com.java.sadna.backend.sportshop.entity.id.ProductStockId;
import com.java.sadna.backend.sportshop.exception.BadRequestException;
import com.java.sadna.backend.sportshop.exception.ConflictException;
import com.java.sadna.backend.sportshop.exception.NotFoundException;
import com.java.sadna.backend.sportshop.mapper.CartViewRowDtoToCartItemDtoMapper;
import com.java.sadna.backend.sportshop.model.CartCountDto;
import com.java.sadna.backend.sportshop.model.CartItemDto;
import com.java.sadna.backend.sportshop.model.CartValidationResultDto;
import com.java.sadna.backend.sportshop.model.CartViewDto;
import com.java.sadna.backend.sportshop.model.CartViewRowDto;
import com.java.sadna.backend.sportshop.model.StockIssueDto;
import com.java.sadna.backend.sportshop.model.enums.StockIssueKind;
import com.java.sadna.backend.sportshop.model.VersionMismatchDto;
import com.java.sadna.backend.sportshop.repository.CartItemRepository;
import com.java.sadna.backend.sportshop.repository.ProductRepository;
import com.java.sadna.backend.sportshop.repository.ProductStockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final CartViewRowDtoToCartItemDtoMapper cartViewRowDtoToCartItemDtoMapper;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       ProductStockRepository productStockRepository,
                       CartViewRowDtoToCartItemDtoMapper cartViewRowDtoToCartItemDtoMapper) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productStockRepository = productStockRepository;
        this.cartViewRowDtoToCartItemDtoMapper = cartViewRowDtoToCartItemDtoMapper;
    }

    @Transactional(readOnly = true)
    public CartCountDto getCount(Long userId) {
        return new CartCountDto((int) cartItemRepository.countByUserId(userId));
    }

    @Transactional(readOnly = true)
    public CartViewDto read(Long userId) {
        return composeCartView(cartItemRepository.findCartViewRowsByUserId(userId));
    }

    @Transactional
    public CartViewDto sync(Long userId) {
        cartItemRepository.bulkSyncVersionsByUserId(userId);
        return composeCartView(cartItemRepository.findCartViewRowsByUserId(userId));
    }

    @Transactional
    public void addItem(Long userId, Long productId, String size, int requestedQuantity) {
        if (requestedQuantity < 1) {
            throw new BadRequestException("cart.qtyMinAdd");
        }

        // Pre-validations surface precise messages in the sequential case. Any flip between
        // these checks and the write falls through to the generic upsert-rejected conflict.

        productRepository.findById(productId)
                .filter(p -> !p.isArchived())
                .orElseThrow(() -> new NotFoundException("cart.productUnavailable"));

        ProductStockEntity stock = productStockRepository
                .findById(new ProductStockId(productId, size))
                .orElseThrow(() -> new NotFoundException("cart.sizeUnavailable"));

        int existingQuantity = cartItemRepository
                .findById(new CartItemId(userId, productId, size))
                .map(ci -> ci.getQuantity())
                .orElse(0);

        if (stock.getQuantity() < existingQuantity + requestedQuantity) {
            throw new ConflictException("cart.insufficientStock");
        }

        int affected = cartItemRepository.upsertIfStockAllows(userId, productId, size, requestedQuantity);
        if (affected == 0) {
            throw new ConflictException("cart.addFailed");
        }
    }

    @Transactional
    public void updateQuantity(Long userId, Long productId, String size, int quantity) {
        if (quantity < 1) {
            throw new BadRequestException("cart.qtyMinPatch");
        }

        // PATCH only adjusts quantity. Archive / stock issues surface at validate / checkout time.
        int updated = cartItemRepository.updateQuantityByCompositeKey(userId, productId, size, quantity);
        if (updated == 0) {
            throw new NotFoundException("cart.itemNotFound");
        }
    }

    @Transactional
    public void removeItem(Long userId, Long productId, String size) {
        cartItemRepository.deleteByCompositeKey(userId, productId, size);
    }

    @Transactional(readOnly = true)
    public CartValidationResultDto validateForCheckout(Long userId) {
        List<CartViewRowDto> rawRows = cartItemRepository.findCartViewRowsByUserId(userId);
        List<CartViewRowDto> rows = dropHardDeletedProductRows(rawRows, userId);

        if (rows.isEmpty()) {
            throw new ConflictException("cart.empty");
        }

        List<VersionMismatchDto> versionMismatches = new ArrayList<>();
        List<StockIssueDto> stockIssues = new ArrayList<>();
        rows.forEach(row -> {
            collectVersionMismatch(row).ifPresent(versionMismatches::add);
            collectStockIssue(row).ifPresent(stockIssues::add);
        });

        CartViewDto cart = composeCartViewFromCleanRows(rows);
        boolean ok = versionMismatches.isEmpty() && stockIssues.isEmpty();
        return new CartValidationResultDto(ok, versionMismatches, stockIssues, cart);
    }

    // --- helpers ---

    private CartViewDto composeCartView(List<CartViewRowDto> rawRows) {
        return composeCartViewFromCleanRows(dropHardDeletedProductRows(rawRows, null));
    }

    private CartViewDto composeCartViewFromCleanRows(List<CartViewRowDto> rows) {
        List<CartItemDto> items = rows.stream().map(cartViewRowDtoToCartItemDtoMapper::map).toList();
        BigDecimal subtotal = items.stream()
                .map(CartItemDto::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartViewDto(items, items.size(), subtotal);
    }

    // Defensive: FK should make this unreachable. If a row leaks through, drop + WARN.
    private List<CartViewRowDto> dropHardDeletedProductRows(List<CartViewRowDto> rows, Long userId) {
        List<CartViewRowDto> trimmed = new ArrayList<>(rows.size());
        for (CartViewRowDto row : rows) {
            if (row.getProductPk() == null) {
                log.warn("Dropping cart row pointing at hard-deleted product (user={}, productId={}, size={}); "
                                + "this should be unreachable -- FK constraint on cart_items.product_id is in place",
                        userId, row.getProductId(), row.getSize());
                continue;
            }
            trimmed.add(row);
        }
        return trimmed;
    }

    private static java.util.Optional<VersionMismatchDto> collectVersionMismatch(CartViewRowDto row) {
        boolean archived = Boolean.TRUE.equals(row.getProductArchived());
        Integer inCart = row.getProductVersionInCart();
        Integer current = row.getProductVersionCurrent();
        boolean drifted = inCart != null && current != null && !inCart.equals(current);
        if (!archived && !drifted) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new VersionMismatchDto(
                row.getProductId(),
                row.getProductName(),
                row.getSize(),
                archived
        ));
    }

    private static java.util.Optional<StockIssueDto> collectStockIssue(CartViewRowDto row) {
        Integer available = row.getAvailableStock();
        int requested = row.getQuantity() != null ? row.getQuantity() : 0;

        if (available == null || available == 0) {
            return java.util.Optional.of(new StockIssueDto(
                    row.getProductId(),
                    row.getProductName(),
                    row.getSize(),
                    StockIssueKind.OUT_OF_STOCK,
                    available == null ? 0 : available,
                    requested
            ));
        }
        if (available < requested) {
            return java.util.Optional.of(new StockIssueDto(
                    row.getProductId(),
                    row.getProductName(),
                    row.getSize(),
                    StockIssueKind.INSUFFICIENT_STOCK,
                    available,
                    requested
            ));
        }
        return java.util.Optional.empty();
    }
}
