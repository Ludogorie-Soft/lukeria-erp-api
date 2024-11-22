ALTER TABLE shopping_carts
    MODIFY order_id BIGINT,
    MODIFY created_by_user BIGINT,
    MODIFY order_date DATE ,
    MODIFY status VARCHAR(50),
    MODIFY is_deleted TINYINT(1) default false;