SELECT year, month, count(member_id) AS active_users
FROM
(
    SELECT member_id, EXTRACT (year from created_at) AS year, EXTRACT (month from created_at) AS month
    FROM page_views
    GROUP BY member_id, year, month
) a
GROUP BY year, month
ORDER BY year, month
