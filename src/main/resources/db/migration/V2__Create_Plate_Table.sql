CREATE TABLE plate (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  available_quantity INT,
  photo VARCHAR(255),
  price DOUBLE,
  is_deleted TINYINT(1) DEFAULT FALSE
);