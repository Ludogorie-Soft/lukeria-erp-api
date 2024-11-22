CREATE TABLE shopping_carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    created_by_user BIGINT NOT NULL,
    order_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES order_table (id),
    FOREIGN KEY (client_id) REFERENCES client(id)
);
