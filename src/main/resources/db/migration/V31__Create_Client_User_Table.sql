CREATE TABLE IF NOT EXISTS client_user (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
    client_id BIGINT,
    user_id BIGINT,
    is_deleted TINYINT(1) DEFAULT FALSE,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);