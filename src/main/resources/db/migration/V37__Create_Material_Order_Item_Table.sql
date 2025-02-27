CREATE TABLE material_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_type VARCHAR(20) NOT NULL,
    material_id BIGINT NOT NULL,
    ordered_quantity INT NOT NULL,
    received_quantity INT DEFAULT 0,
    material_name VARCHAR(255),  -- ✅ Exists in the entity
    photo VARCHAR(255),  -- ✅ Exists in the entity
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES material_order(id) ON DELETE CASCADE
);
