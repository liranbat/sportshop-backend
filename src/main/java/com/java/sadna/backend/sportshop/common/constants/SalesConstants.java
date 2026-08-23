package com.java.sadna.backend.sportshop.common.constants;

import com.java.sadna.backend.sportshop.common.util.OrderStatusTransitions;

import java.util.Set;

public final class SalesConstants {

    public static final Set<String> CANCELLED_STATUSES = Set.of(
            OrderStatusTransitions.CANCELLED_BY_USER,
            OrderStatusTransitions.CANCELLED_BY_ADMIN);

    public static final class TopProductsSort {
        public static final String REVENUE = "revenue";
        public static final String QUANTITY = "quantity";

        private TopProductsSort() {}
    }

    private SalesConstants() {}
}
