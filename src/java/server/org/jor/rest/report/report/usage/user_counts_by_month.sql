SELECT year, month, COUNT(member_id) AS new_users
FROM
(
    SELECT id AS member_id, EXTRACT (year from created_at) AS year, EXTRACT (month from created_at) AS month
    FROM members m
    WHERE m.confirmed_at IS NOT NULL
) a
GROUP BY year, month
