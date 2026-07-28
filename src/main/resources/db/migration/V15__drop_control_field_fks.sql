ALTER TABLE users    DROP CONSTRAINT IF EXISTS users_deleted_by_fk;
ALTER TABLE users    DROP CONSTRAINT IF EXISTS users_updated_by_fk;
ALTER TABLE products DROP CONSTRAINT IF EXISTS products_archived_by_fkey;
ALTER TABLE orders   DROP CONSTRAINT IF EXISTS orders_cancelled_by_fkey;
ALTER TABLE orders   DROP CONSTRAINT IF EXISTS orders_updated_by_fkey;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_updated_by_fkey;
