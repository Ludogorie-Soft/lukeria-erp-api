
CREATE TABLE password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Индекс за по-бързо търсене по токен (опционално)
CREATE INDEX idx_password_reset_token_token ON password_reset_token(token);
