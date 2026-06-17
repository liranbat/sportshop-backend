package com.java.sadna.backend.sportshop.util;

import java.util.Map;
import java.util.Set;

// Source of truth for admin-driven status changes (POST /admin/orders/{n}/update-status).
// Cancel paths live in OrderService.ADMIN_CANCELLABLE_STATUSES -- this table covers
// PAID / SHIPPED / DELIVERED / DONE only. Terminal statuses (DONE, CANCELLED_*) and
// self-loops are absent by design.
public final class OrderStatusTransitions {

    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_SHIPPED = "SHIPPED";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_DONE = "DONE";

    private static final Map<String, Set<String>> ALLOWED = Map.of(
            STATUS_PAID,      Set.of(STATUS_SHIPPED, STATUS_DELIVERED, STATUS_DONE),
            STATUS_SHIPPED,   Set.of(STATUS_PAID,    STATUS_DELIVERED, STATUS_DONE),
            STATUS_DELIVERED, Set.of(STATUS_PAID,    STATUS_SHIPPED,   STATUS_DONE)
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
