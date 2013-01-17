SELECT t.year, t.month,
    count(*) AS total,
    SUM(CASE WHEN (is_confirmed = false OR is_confirmed IS NULL) THEN 1 ELSE 0 END) AS unconfirmed,
    SUM(CASE WHEN (is_confirmed = true) THEN 1 ELSE 0 END) AS confirmed
FROM member_dimension m INNER JOIN time_dimension t ON m.member_created_at = t.id 
GROUP BY t.year, t.month
ORDER BY t.year, t.month
