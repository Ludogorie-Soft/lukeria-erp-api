ALTER TABLE material_order_item
ADD CONSTRAINT fk_material_order FOREIGN KEY (order_id) REFERENCES material_order(id) ON DELETE CASCADE;
