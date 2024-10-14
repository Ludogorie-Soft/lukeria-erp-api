CREATE TABLE IF NOT EXISTS invoice_order_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT,
    order_product_id BIGINT,
    is_deleted TINYINT(1) DEFAULT FALSE,
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    FOREIGN KEY (order_product_id) REFERENCES order_product(id)
);