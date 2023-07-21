CREATE TABLE material_order (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ordered_quantity INT NOT NULL,
    received_quantity INT,
    material_id BIGINT NOT NULL,
    material_type VARCHAR(50) NOT NULL,
    material_price DECIMAL(10, 2),
    arrival_date DATE,
     is_deleted TINYINT(1) DEFAULT FALSE
);