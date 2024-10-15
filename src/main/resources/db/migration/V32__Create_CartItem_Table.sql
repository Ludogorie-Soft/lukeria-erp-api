CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    deleted TINYINT(1) DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);