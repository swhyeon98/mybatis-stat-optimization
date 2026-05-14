-- MySQL performance_schema examples for checking statement execution count and time.
-- Timer values are stored in picoseconds. Divide by 1,000,000,000 to read milliseconds.

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

-- Optional reset before a new run.
-- TRUNCATE TABLE performance_schema.events_statements_summary_by_digest;
