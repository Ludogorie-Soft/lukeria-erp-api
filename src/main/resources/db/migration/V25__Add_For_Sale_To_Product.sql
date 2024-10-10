-- Check and add for_sale column
SET @column_exists_for_sale := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'product'
  AND COLUMN_NAME = 'for_sale'
);

SET @sql_for_sale := IF(@column_exists_for_sale = 0,
  'ALTER TABLE product ADD COLUMN for_sale BOOLEAN DEFAULT false;',
  'SELECT "Column for_sale already exists."');

PREPARE stmt_for_sale FROM @sql_for_sale;
EXECUTE stmt_for_sale;
DEALLOCATE PREPARE stmt_for_sale;
