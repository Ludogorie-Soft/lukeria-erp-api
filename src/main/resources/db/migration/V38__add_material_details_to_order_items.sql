-- Add new columns to material_order_item table
ALTER TABLE material_order_item
    ADD COLUMN material_name VARCHAR(255),
    ADD COLUMN material_code VARCHAR(255),
    ADD COLUMN photo VARCHAR(255);