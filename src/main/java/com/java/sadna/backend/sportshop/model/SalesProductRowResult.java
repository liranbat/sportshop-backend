package com.java.sadna.backend.sportshop.model;

import java.math.BigDecimal;

// One aggregated top-product row. `result` is whichever metric the query ranked by.
public interface SalesProductRowResult {

    Long getProductId();

    BigDecimal getResult();
}
