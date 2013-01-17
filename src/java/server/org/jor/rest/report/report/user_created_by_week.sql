SELECT member_year AS user_created_year, member_week AS user_create_week, COUNT(member_id) AS user_count
FROM
(
    SELECT m.id AS member_id, t.iso_year AS member_year, t.iso_week AS member_week
    FROM member_dimension m INNER JOIN time_dimension t ON (m.member_created_at = t.id)
    WHERE EXTRACT (epoch FROM (now() - interval '60 days')) < m.member_created_at
) a
GROUP BY member_year, member_week
ORDER BY member_year, member_week
