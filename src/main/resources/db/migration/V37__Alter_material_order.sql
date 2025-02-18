ALTER TABLE material_order
    DROP COLUMN ordered_quantity,
    DROP COLUMN received_quantity,
    DROP COLUMN material_id,
    DROP COLUMN material_type,
    DROP COLUMN material_price,
    ADD COLUMN order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
-- Create material_order_item table with foreign key
CREATE TABLE material_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_type VARCHAR(20) NOT NULL,
    material_id BIGINT NOT NULL,
    ordered_quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES material_order(id) ON DELETE CASCADE
);