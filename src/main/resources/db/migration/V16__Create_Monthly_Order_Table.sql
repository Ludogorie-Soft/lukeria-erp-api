CREATE TABLE IF NOT EXISTS monthly_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT,
    start_date DATE,
    end_date DATE,
    is_invoiced TINYINT(1) DEFAULT FALSE,
    is_deleted BOOLEAN,
    FOREIGN KEY (client_id) REFERENCES client(id)
);