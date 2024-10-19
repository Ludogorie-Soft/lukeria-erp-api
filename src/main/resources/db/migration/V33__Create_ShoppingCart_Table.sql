CREATE TABLE shopping_carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    client_id BIGINT,
    created_by_user BIGINT,
    order_date DATE,
    status VARCHAR(50),
    is_deleted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES order_table (id),
    FOREIGN KEY (client_id) REFERENCES client(id)
);
