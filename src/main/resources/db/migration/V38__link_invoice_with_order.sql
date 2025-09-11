-- V__38__link_invoice_with_order.sql

-- 1️⃣ Попълваме order_id за фактурите, които са свързани само с един ордер
UPDATE invoice i
JOIN (
    SELECT iop.invoice_id, MIN(op.order_id) as order_id
    FROM invoice_order_product iop
    JOIN order_product op ON iop.order_product_id = op.id
    GROUP BY iop.invoice_id
    HAVING COUNT(DISTINCT op.order_id) = 1
) x ON i.id = x.invoice_id
SET i.order_id = x.order_id
WHERE i.order_id IS NULL;
