EXPLAIN ANALYZE
SELECT
    COUNT(DISTINCT i.id) AS total_count,
    COALESCE(SUM(CASE WHEN i.status = 'RECEIVED' THEN 1 ELSE 0 END), 0) AS received_count,
    COALESCE(SUM(CASE WHEN i.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), 0) AS in_progress_count,
    COALESCE(SUM(CASE WHEN i.status = 'RESOLVED' THEN 1 ELSE 0 END), 0) AS resolved_count,
    COALESCE(SUM(CASE WHEN i.status = 'ON_HOLD' THEN 1 ELSE 0 END), 0) AS on_hold_count,
    COALESCE(SUM(CASE WHEN i.status = 'REOPENED' THEN 1 ELSE 0 END), 0) AS reopened_count,
    COALESCE(SUM(CASE WHEN i.status = 'TRANSFERRED' THEN 1 ELSE 0 END), 0) AS transferred_count,
    COALESCE(SUM(CASE WHEN i.status IS NULL OR i.status != 'RESOLVED' THEN 1 ELSE 0 END), 0) AS unresolved_count
FROM customer_inquiry i
WHERE i.category = 'PAYMENT_REFUND';

EXPLAIN ANALYZE
SELECT
    COUNT(DISTINCT i.id) AS total_count,
    COALESCE(SUM(CASE WHEN i.status = 'RECEIVED' THEN 1 ELSE 0 END), 0) AS received_count,
    COALESCE(SUM(CASE WHEN i.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), 0) AS in_progress_count,
    COALESCE(SUM(CASE WHEN i.status = 'RESOLVED' THEN 1 ELSE 0 END), 0) AS resolved_count,
    COALESCE(SUM(CASE WHEN i.status = 'ON_HOLD' THEN 1 ELSE 0 END), 0) AS on_hold_count,
    COALESCE(SUM(CASE WHEN i.status = 'REOPENED' THEN 1 ELSE 0 END), 0) AS reopened_count,
    COALESCE(SUM(CASE WHEN i.status = 'TRANSFERRED' THEN 1 ELSE 0 END), 0) AS transferred_count,
    COALESCE(SUM(CASE WHEN i.status IS NULL OR i.status != 'RESOLVED' THEN 1 ELSE 0 END), 0) AS unresolved_count
FROM customer_inquiry i;
