SET @column_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'package'
  AND COLUMN_NAME = 'english_name'
);

SET @sql := IF(@column_exists = 0,
  'ALTER TABLE package ADD COLUMN english_name VARCHAR(255);',
  'SELECT "Column already exists."');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
