DROP INDEX idx_refresh_tokens_last_used_at;
ALTER TABLE refresh_tokens DROP COLUMN last_used_at;
