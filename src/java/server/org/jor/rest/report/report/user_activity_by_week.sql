SELECT member_year AS user_created_year, member_week AS user_create_week, activity_week, COUNT(member_id) AS user_count
FROM
(
    SELECT DISTINCT e.member_id AS member_id, mt.iso_year AS member_year, mt.iso_week AS member_week,
           ((et.id - mt.id) / (7 * 24 * 3600)) AS activity_week
    FROM events e
         INNER JOIN time_dimension et ON e.event_time = et.id
         INNER JOIN time_dimension mt ON e.member_created_at = mt.id
    WHERE EXTRACT (epoch FROM (now() - interval '60 days')) <= e.event_time
      AND EXTRACT (epoch FROM (now() - interval '60 days')) <= e.member_created_at
) a
GROUP BY member_year, member_week, activity_week
