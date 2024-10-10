CREATE TABLE IF NOT EXISTS customer_custom_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT,
    product_id BIGINT,
    price DECIMAL(10, 2) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);