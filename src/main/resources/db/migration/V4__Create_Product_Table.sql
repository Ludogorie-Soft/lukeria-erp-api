CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  price DECIMAL(10, 2) NOT NULL,
  package_id BIGINT,
  FOREIGN KEY (package_id) REFERENCES package(id),
  available_quantity INT NOT NULL,
  is_deleted TINYINT(1) DEFAULT FALSE
);