CREATE TABLE IF NOT EXISTS order_table  (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  client_id BIGINT,
  order_date DATE,
  FOREIGN KEY (client_id) REFERENCES client(id),
  is_deleted TINYINT(1) DEFAULT FALSE,
  is_invoiced TINYINT(1) DEFAULT FALSE
);