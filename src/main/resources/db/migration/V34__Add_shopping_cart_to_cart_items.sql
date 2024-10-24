ALTER TABLE cart_items
ADD COLUMN shopping_cart_id BIGINT,
ADD CONSTRAINT fk_shopping_cart
        FOREIGN KEY (shopping_cart_id)
        REFERENCES shopping_carts(id)
        ON DELETE CASCADE
