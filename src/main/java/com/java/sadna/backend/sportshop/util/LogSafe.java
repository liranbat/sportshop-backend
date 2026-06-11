package com.java.sadna.backend.sportshop.util;

public final class LogSafe {

    private static final String MASK = "****";

    private LogSafe() {
    }

    public static String cardLast4(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return MASK;
        }
        return MASK + cardNumber.substring(cardNumber.length() - 4);
    }
}
