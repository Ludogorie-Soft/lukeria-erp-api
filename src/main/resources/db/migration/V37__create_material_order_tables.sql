-- Create material_order table
CREATE TABLE material_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    arrival_date DATE,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- Create material_order_item table with foreign key
CREATE TABLE material_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_type VARCHAR(20) NOT NULL,
    material_id BIGINT NOT NULL,
    ordered_quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES material_order(id) ON DELETE CASCADE
);