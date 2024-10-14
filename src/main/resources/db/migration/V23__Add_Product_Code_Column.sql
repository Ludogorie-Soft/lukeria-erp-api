-- Check and add product_code column
SET @column_exists_product_code := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'package'
  AND COLUMN_NAME = 'product_code'
);

SET @sql_product_code := IF(@column_exists_product_code = 0,
  'ALTER TABLE package ADD COLUMN product_code VARCHAR(255);',
  'SELECT "Column product_code already exists."');

PREPARE stmt_product_code FROM @sql_product_code;
EXECUTE stmt_product_code;
DEALLOCATE PREPARE stmt_product_code;
