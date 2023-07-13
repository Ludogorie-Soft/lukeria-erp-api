CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  price DOUBLE NOT NULL,
  available_quantity INT NOT NULL,
  plate_id BIGINT,
  FOREIGN KEY (plate_id) REFERENCES plate(id),
  is_deleted TINYINT(1) DEFAULT FALSE
);