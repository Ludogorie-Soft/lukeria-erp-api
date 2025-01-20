CREATE TABLE produced_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT,
    quantity INT NOT NULL,
    manufacture_date TIMESTAMP,
    is_deleted TINYINT(1) DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);