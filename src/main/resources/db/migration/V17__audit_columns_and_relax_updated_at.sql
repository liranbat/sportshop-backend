-- Adding audit columns.
ALTER TABLE orders
    ADD COLUMN cancelled_by BIGINT REFERENCES users(id);

ALTER TABLE payments
    ADD COLUMN updated_by BIGINT REFERENCES users(id);

-- Making updated_at nullable and not inserted by default.
ALTER TABLE orders
    ALTER COLUMN updated_at DROP DEFAULT,
    ALTER COLUMN updated_at DROP NOT NULL;

ALTER TABLE payments
    ALTER COLUMN updated_at DROP DEFAULT,
    ALTER COLUMN updated_at DROP NOT NULL;

ALTER TABLE categories
    ALTER COLUMN updated_at DROP DEFAULT,
    ALTER COLUMN updated_at DROP NOT NULL;

-- Cleanup.
UPDATE orders     SET updated_at = NULL WHERE updated_at = created_at;
UPDATE payments   SET updated_at = NULL WHERE updated_at = created_at;
UPDATE categories SET updated_at = NULL;
