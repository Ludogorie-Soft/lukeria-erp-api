CREATE TABLE monthly_order_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    ordered_quantity INT,
    sent_quantity INT,
    monthly_order_id BIGINT,
    is_deleted BOOLEAN,
    FOREIGN KEY (monthly_order_id) REFERENCES monthly_order(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);