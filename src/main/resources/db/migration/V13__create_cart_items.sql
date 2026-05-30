CREATE TABLE cart_items (
    user_id         BIGINT       NOT NULL REFERENCES users(id),
    product_id      BIGINT       NOT NULL REFERENCES products(id),
    size            VARCHAR(20)  NOT NULL,
    quantity        INTEGER      NOT NULL CHECK (quantity >= 1),
    product_version INTEGER      NOT NULL CHECK (product_version >= 1),
    PRIMARY KEY (user_id, product_id, size)
);
