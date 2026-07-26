package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.common.constants.ErrorConstants;
import com.java.sadna.backend.sportshop.config.PaymentProperties;
import com.java.sadna.backend.sportshop.exception.BadGatewayException;
import com.java.sadna.backend.sportshop.model.PaymentDetailsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class MockPaymentProcessor {

    private static final String DECLINE_REASON = "CARD_DECLINE";
    private static final String TRANSACTION_PREFIX = "MOCK-";
    private static final String CARD_MASK = "****";

    private final String declineSuffix;

    public MockPaymentProcessor(PaymentProperties paymentProperties) {
        this.declineSuffix = paymentProperties.getMock().getDeclineSuffix();
    }

    // Card numbers ending in the mock decline suffix (0000 by default) are declined;
    // everything else returns a synthetic transaction id.
    public String process(PaymentDetailsDto payment, BigDecimal amount) {
        log.info("Payment attempt: cardLast4={} amount={}", maskCardForLog(payment.getCardNumber()), amount);
        if (payment.getCardNumber() != null && payment.getCardNumber().endsWith(declineSuffix)) {
            log.warn("Payment declined: reasonCode={}", DECLINE_REASON);
            throw new BadGatewayException(ErrorConstants.Payment.DECLINED);
        }
        String transactionId = TRANSACTION_PREFIX + UUID.randomUUID();
        log.info("Payment result: status=SUCCESS transactionId={}", transactionId);
        return transactionId;
    }

    // never log full card numbers; mask everything but the last 4 digits
    private static String maskCardForLog(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return CARD_MASK;
        }
        return CARD_MASK + cardNumber.substring(cardNumber.length() - 4);
    }
}
