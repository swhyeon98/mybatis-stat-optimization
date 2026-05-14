-- Representative v1/v2 cell-level COUNT query.
-- Run after the application has created sample data.

EXPLAIN ANALYZE
SELECT COUNT(DISTINCT i.id)
FROM customer_inquiry i
WHERE i.category = 'PAYMENT_REFUND'
  AND i.status = 'RECEIVED';

EXPLAIN ANALYZE
SELECT COUNT(DISTINCT i.id)
FROM customer_inquiry i
WHERE i.category = 'PAYMENT_REFUND'
  AND i.status != 'RESOLVED';
