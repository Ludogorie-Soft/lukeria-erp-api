CREATE TABLE image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name BINARY(16),
    package_id BIGINT,
    upload_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME,
     FOREIGN KEY (package_id) REFERENCES package(id)

);
