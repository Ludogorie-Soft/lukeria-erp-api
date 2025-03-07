INSERT INTO material_order_item (material_type, material_id, ordered_quantity, received_quantity, order_id)
SELECT
    COALESCE(mo.material_type, 'UNKNOWN'),  -- Handle missing values
    COALESCE(mo.material_id, 0),
    COALESCE(mo.ordered_quantity, 0),
    COALESCE(mo.received_quantity, 0),
    mo.id
FROM material_order mo;
