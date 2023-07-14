CREATE TABLE package (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  available_quantity INT,
  carton_id BIGINT,
  plate_id BIGINT,
  pieces_carton INT,
  photo VARCHAR(255),
  price DOUBLE,
  FOREIGN KEY (plate_id) REFERENCES plate(id),
  FOREIGN KEY (carton_id) REFERENCES carton(id),
  is_deleted TINYINT(1) DEFAULT FALSE
);