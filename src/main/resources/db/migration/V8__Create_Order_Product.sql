CREATE TABLE order_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    number INT NOT NULL,
    order_id BIGINT,
    package_id BIGINT,
    is_deleted TINYINT(1) DEFAULT FALSE,
    FOREIGN KEY (order_id) REFERENCES order_table(id),
    FOREIGN KEY (package_id) REFERENCES package(id),
     selling_price INT
);