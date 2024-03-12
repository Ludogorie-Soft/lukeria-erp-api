CREATE TABLE image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name BINARY(16),
    package_id BIGINT,
    plate_id BIGINT,
    upload_at DATETIME DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (package_id) REFERENCES package(id),
     FOREIGN KEY (plate_id) REFERENCES plate(id)


);
