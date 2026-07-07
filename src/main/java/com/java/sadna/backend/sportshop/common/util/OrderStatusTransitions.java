package com.java.sadna.backend.sportshop.common.util;

import java.util.Map;
import java.util.Set;

public final class OrderStatusTransitions {

    public enum Value {
        PAID,
        SHIPPED,
        DELIVERED,
        DONE,
        CANCELLED_BY_USER,
        CANCELLED_BY_ADMIN
    }

    public static final String PAID = Value.PAID.name();
    public static final String SHIPPED = Value.SHIPPED.name();
    public static final String DELIVERED = Value.DELIVERED.name();
    public static final String DONE = Value.DONE.name();
    public static final String CANCELLED_BY_USER = Value.CANCELLED_BY_USER.name();
    public static final String CANCELLED_BY_ADMIN = Value.CANCELLED_BY_ADMIN.name();

    private static final Map<String, Set<String>> ALLOWED = Map.of(
            PAID,      Set.of(SHIPPED, DELIVERED, DONE),
            SHIPPED,   Set.of(PAID,    DELIVERED, DONE),
            DELIVERED, Set.of(PAID,    SHIPPED,   DONE)
    );

    private OrderStatusTransitions() {}

    public static boolean isAllowed(String priorStatus, String targetStatus) {
        if (priorStatus == null || targetStatus == null) {
            return false;
        }
        Set<String> targets = ALLOWED.get(priorStatus);
        return targets != null && targets.contains(targetStatus);
    }
}
