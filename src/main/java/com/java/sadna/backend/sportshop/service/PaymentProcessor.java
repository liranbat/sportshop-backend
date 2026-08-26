package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.model.PaymentDetailsDto;

import java.math.BigDecimal;

public interface PaymentProcessor {

    String charge(PaymentDetailsDto payment, BigDecimal amount, String currency);

    String refund(String transactionId);
}
