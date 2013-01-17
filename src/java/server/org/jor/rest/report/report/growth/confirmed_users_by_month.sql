SELECT
    EXTRACT (year FROM created_at) AS year, EXTRACT (month FROM created_at) AS month,
    count(*) AS total,
    SUM(CASE WHEN (confirmed_at IS NULL) THEN 1 ELSE 0 END) AS unconfirmed,
    SUM(CASE WHEN (confirmed_at IS NOT NULL) THEN 1 ELSE 0 END) AS confirmed
FROM members m 
GROUP BY EXTRACT (year FROM created_at), EXTRACT (month FROM created_at)
ORDER BY EXTRACT (year FROM created_at), EXTRACT (month FROM created_at)
