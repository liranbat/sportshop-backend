ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS refund_transaction_id VARCHAR(100);
