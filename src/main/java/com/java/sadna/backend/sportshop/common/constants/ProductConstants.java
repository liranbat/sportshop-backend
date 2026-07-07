package com.java.sadna.backend.sportshop.common.constants;

public final class ProductConstants {

    // Sentinel size for single-size products (is_multi_size = false). Every stock row for
    // a single-size product uses this token; multi-size products cannot use it.
    public static final String ONE_SIZE_TOKEN = "ONE_SIZE";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String ARCHIVED = "archived";
    public static final String MULTI_SIZE = "multiSize";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY = "category";
    public static final String UPDATED_AT = "updatedAt";

    public static final class Sort {
        public static final String PRICE = "price";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String UPDATED_AT = "updatedAt";

        private Sort() {}
    }

    private ProductConstants() {
    }
}
