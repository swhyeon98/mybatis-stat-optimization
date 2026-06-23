EXPLAIN ANALYZE
SELECT COUNT(DISTINCT i.id)
FROM customer_inquiry i
WHERE i.category = 'PAYMENT_REFUND'
  AND i.status = 'RECEIVED';

EXPLAIN ANALYZE
SELECT COUNT(DISTINCT i.id)
FROM customer_inquiry i
WHERE i.category = 'PAYMENT_REFUND'
  AND (i.status IS NULL OR i.status != 'RESOLVED');
