-- 1️⃣ Добавяне на колоната order_id в invoice
ALTER TABLE invoice
ADD COLUMN order_id BIGINT;

-- 2️⃣ Добавяне на foreign key връзка към order_table
ALTER TABLE invoice
ADD CONSTRAINT fk_invoice_order
FOREIGN KEY (order_id) REFERENCES order_table(id);
