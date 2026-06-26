SELECT
    DIGEST_TEXT,
    COUNT_STAR,
    ROUND(SUM_TIMER_WAIT / 1000000000, 2) AS sum_timer_ms,
    ROUND(AVG_TIMER_WAIT / 1000000000, 2) AS avg_timer_ms,
    SUM_ROWS_EXAMINED,
    SUM_ROWS_SENT
FROM performance_schema.events_statements_summary_by_digest
WHERE DIGEST_TEXT LIKE '%customer_inquiry%'
ORDER BY SUM_TIMER_WAIT DESC
LIMIT 20;
