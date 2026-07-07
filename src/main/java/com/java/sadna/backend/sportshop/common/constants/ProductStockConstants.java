package com.java.sadna.backend.sportshop.common.constants;

public final class ProductStockConstants {

    public static final String PRODUCT = "product";
    public static final String PRODUCT_ID = "productId";
    public static final String SIZE = "size";
    public static final String QUANTITY = "quantity";
    public static final String LOW_STOCK_THRESHOLD = "lowStockThreshold";

    public static final class Sort {
        public static final String NAME = "name";
        public static final String QUANTITY = "quantity";
        public static final String THRESHOLD = "threshold";

        private Sort() {}
    }

    private ProductStockConstants() {}
}
