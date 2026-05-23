CREATE TABLE product_stock (
    product_id           BIGINT        NOT NULL REFERENCES products(id),
    size                 VARCHAR(20)   NOT NULL,
    quantity             INTEGER       NOT NULL CHECK (quantity >= 0),
    low_stock_threshold  INTEGER       NULL     CHECK (low_stock_threshold IS NULL OR low_stock_threshold >= 0),
    PRIMARY KEY (product_id, size)
);

CREATE INDEX idx_product_stock_product
    ON product_stock (product_id);
