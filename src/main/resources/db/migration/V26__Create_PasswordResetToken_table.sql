CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Check if the index already exists
SET @index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'password_reset_token'
    AND INDEX_NAME = 'idx_password_reset_token_token'
);

SET @sql_index := IF(@index_exists = 0,
    'CREATE INDEX idx_password_reset_token_token ON password_reset_token(token);',
    'SELECT "Index idx_password_reset_token_token already exists."');

PREPARE stmt_index FROM @sql_index;
EXECUTE stmt_index;
DEALLOCATE PREPARE stmt_index;
