-- Check if the information column exists
SET @column_exists_information := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'client'
    AND COLUMN_NAME = 'information'
);

SET @sql_information := IF(@column_exists_information = 0,
  'ALTER TABLE client ADD COLUMN information VARCHAR(255);',
  'SELECT "Column information already exists."');

PREPARE stmt_information FROM @sql_information;
EXECUTE stmt_information;
DEALLOCATE PREPARE stmt_information;
