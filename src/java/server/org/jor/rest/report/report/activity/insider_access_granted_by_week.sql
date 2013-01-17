SELECT member_year AS access_granted_year, member_week AS access_granted_week, COUNT(member_id) AS user_count
FROM
(
    SELECT m.id AS member_id, t.iso_year AS member_year, t.iso_week AS member_week
    FROM member_dimension m INNER JOIN time_dimension t ON (m.access_granted_at = t.id)
    WHERE EXTRACT (epoch FROM (now() - interval '60 days')) < m.access_granted_at
      AND m.access_granted_at is not null
) a
GROUP BY member_year, member_week
ORDER BY member_year, member_week
