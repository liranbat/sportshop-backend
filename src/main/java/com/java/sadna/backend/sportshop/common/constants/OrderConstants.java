package com.java.sadna.backend.sportshop.common.constants;

public final class OrderConstants {

    public static final String ID = "id";
    public static final String USER = "user";
    public static final String USER_ID = "userId";
    public static final String STATUS = "status";
    public static final String ORDER_NUMBER = "orderNumber";
    public static final String TOTAL_PRICE = "totalPrice";
    public static final String CREATED_AT = "createdAt";

    public static final class Sort {
        public static final String TOTAL = "total";
        public static final String DATE = "date";

        private Sort() {}
    }

    private OrderConstants() {}
}
