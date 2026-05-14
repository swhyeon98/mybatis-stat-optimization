-- v3 aggregate query.
-- The application builds the "전체" row in Java by summing category rows.

EXPLAIN ANALYZE
SELECT
    i.category AS category_code,
    COUNT(DISTINCT i.id) AS total_count,
    SUM(CASE WHEN i.status = 'RECEIVED' THEN 1 ELSE 0 END) AS received_count,
    SUM(CASE WHEN i.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress_count,
    SUM(CASE WHEN i.status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved_count,
    SUM(CASE WHEN i.status = 'ON_HOLD' THEN 1 ELSE 0 END) AS on_hold_count,
    SUM(CASE WHEN i.status = 'REOPENED' THEN 1 ELSE 0 END) AS reopened_count,
    SUM(CASE WHEN i.status = 'TRANSFERRED' THEN 1 ELSE 0 END) AS transferred_count,
    SUM(CASE WHEN i.status != 'RESOLVED' THEN 1 ELSE 0 END) AS unresolved_count
FROM customer_inquiry i
GROUP BY i.category;
