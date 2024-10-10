-- Check and add english_business_name column
SET @column_exists_business_name := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'client'
  AND COLUMN_NAME = 'english_business_name'
);

SET @sql_business_name := IF(@column_exists_business_name = 0,
  'ALTER TABLE client ADD COLUMN english_business_name VARCHAR(255);',
  'SELECT "Column english_business_name already exists."');

PREPARE stmt_business_name FROM @sql_business_name;
EXECUTE stmt_business_name;
DEALLOCATE PREPARE stmt_business_name;

-- Check and add english_address column
SET @column_exists_address := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'client'
  AND COLUMN_NAME = 'english_address'
);

SET @sql_address := IF(@column_exists_address = 0,
  'ALTER TABLE client ADD COLUMN english_address VARCHAR(255);',
  'SELECT "Column english_address already exists."');

PREPARE stmt_address FROM @sql_address;
EXECUTE stmt_address;
DEALLOCATE PREPARE stmt_address;
