CREATE TABLE carton (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  size VARCHAR(255) NOT NULL,
  available_quantity INT,
   price DECIMAL(10, 2),
  is_deleted TINYINT(1) DEFAULT FALSE
);