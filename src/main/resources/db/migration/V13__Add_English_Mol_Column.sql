SET @column_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'client'
  AND COLUMN_NAME = 'english_mol'
);

SET @sql := IF(@column_exists = 0,
  'ALTER TABLE client ADD COLUMN english_mol VARCHAR(255);',
  'SELECT "Column already exists."');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
