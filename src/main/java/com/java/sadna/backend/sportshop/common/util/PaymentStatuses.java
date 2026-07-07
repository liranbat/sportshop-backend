package com.java.sadna.backend.sportshop.common.util;

public final class PaymentStatuses {

    public enum Value {
        SUCCESS,
        REFUNDED
    }

    public static final String SUCCESS = Value.SUCCESS.name();
    public static final String REFUNDED = Value.REFUNDED.name();

    private PaymentStatuses() {}
}
