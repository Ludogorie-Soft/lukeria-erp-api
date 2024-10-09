CREATE TABLE IF NOT EXISTS invoice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  invoice_date DATE,
  invoice_number BIGINT,
  total_price DECIMAL(10, 2) NOT NULL,
  is_cash_payment TINYINT(1) DEFAULT FALSE,
  deadline DATE,
  is_deleted TINYINT(1) DEFAULT FALSE,
  is_created TINYINT(1) DEFAULT FALSE
);