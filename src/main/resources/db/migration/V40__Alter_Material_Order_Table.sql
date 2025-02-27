ALTER TABLE material_order
DROP COLUMN material_type,
DROP COLUMN material_id,
DROP COLUMN ordered_quantity,
DROP COLUMN received_quantity,
DROP COLUMN material_price,
ADD COLUMN order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
