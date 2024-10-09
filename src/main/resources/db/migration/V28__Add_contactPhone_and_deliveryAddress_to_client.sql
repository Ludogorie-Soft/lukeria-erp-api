-- Check if the contact_phone column exists
SET @column_exists_contact_phone := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'client'
    AND COLUMN_NAME = 'contact_phone'
);

SET @sql_contact_phone := IF(@column_exists_contact_phone = 0,
  'ALTER TABLE client ADD COLUMN contact_phone VARCHAR(255);',
  'SELECT "Column contact_phone already exists."');

PREPARE stmt_contact_phone FROM @sql_contact_phone;
EXECUTE stmt_contact_phone;
DEALLOCATE PREPARE stmt_contact_phone;

-- Check if the delivery_address column exists
SET @column_exists_delivery_address := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'client'
    AND COLUMN_NAME = 'delivery_address'
);

SET @sql_delivery_address := IF(@column_exists_delivery_address = 0,
  'ALTER TABLE client ADD COLUMN delivery_address VARCHAR(255);',
  'SELECT "Column delivery_address already exists."');

PREPARE stmt_delivery_address FROM @sql_delivery_address;
EXECUTE stmt_delivery_address;
DEALLOCATE PREPARE stmt_delivery_address;
