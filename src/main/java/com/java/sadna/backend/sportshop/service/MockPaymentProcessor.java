package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.exception.BadGatewayException;
import com.java.sadna.backend.sportshop.model.PaymentDetailsDto;
import com.java.sadna.backend.sportshop.util.LogSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentProcessor.class);

    private static final String DECLINE_SUFFIX = "0000";
    private static final String DECLINE_REASON = "CARD_DECLINE";
    private static final String DECLINE_MESSAGE =
            "Your payment couldn't be processed. Please try again or use a different card.";
    private static final String TRANSACTION_PREFIX = "MOCK-";

    // Card numbers ending in 0000 are declined; everything else returns a synthetic transaction id.
    public String process(PaymentDetailsDto payment, BigDecimal amount) {
        String last4 = LogSafe.cardLast4(payment.getCardNumber());
        log.info("Payment attempt: cardLast4={} amount={}", last4, amount);
        if (payment.getCardNumber() != null && payment.getCardNumber().endsWith(DECLINE_SUFFIX)) {
            log.warn("Payment declined: reasonCode={}", DECLINE_REASON);
            throw new BadGatewayException(DECLINE_MESSAGE);
        }
        String transactionId = TRANSACTION_PREFIX + UUID.randomUUID();
        log.info("Payment result: status=SUCCESS transactionId={}", transactionId);
        return transactionId;
    }
}
