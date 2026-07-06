package com.java.sadna.backend.sportshop.common.constants;

// Product-domain magic strings kept in one place so ProductService, StockService, and any
// future consumer never disagree on the reserved single-size token.
public final class ProductConstants {

    // Sentinel size for single-size products (is_multi_size = false). Every stock row for
    // a single-size product uses this token; multi-size products cannot use it.
    public static final String ONE_SIZE_TOKEN = "ONE_SIZE";

    private ProductConstants() {
    }
}
