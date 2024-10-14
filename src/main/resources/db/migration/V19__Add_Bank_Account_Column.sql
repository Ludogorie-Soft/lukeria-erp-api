-- Check and add bank_account column
SET @column_exists_bank_account := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'invoice'
  AND COLUMN_NAME = 'bank_account'
);

SET @sql_bank_account := IF(@column_exists_bank_account = 0,
  'ALTER TABLE invoice ADD COLUMN bank_account VARCHAR(255);',
  'SELECT "Column bank_account already exists."');

PREPARE stmt_bank_account FROM @sql_bank_account;
EXECUTE stmt_bank_account;
DEALLOCATE PREPARE stmt_bank_account;
