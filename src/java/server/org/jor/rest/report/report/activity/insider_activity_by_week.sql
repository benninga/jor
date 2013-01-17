SELECT access_granted_year, access_granted_week, activity_week, COUNT(member_id) AS user_count
FROM
(
    SELECT DISTINCT e.member_id AS member_id,
           mt.iso_year AS access_granted_year, mt.iso_week AS access_granted_week,
           ((e.event_time - m.access_granted_at) / (7 * 24 * 3600)) AS activity_week
    FROM events e
         INNER JOIN time_dimension et ON e.event_time = et.id
         INNER JOIN member_dimension m ON (e.member_id = m.id AND m.access_granted_at IS NOT NULL)
         INNER JOIN time_dimension mt ON m.access_granted_at = mt.id
         INNER JOIN project_dimension p ON (e.project_id = p.id AND p.is_private = true)
    WHERE EXTRACT (epoch FROM (now() - interval '60 days')) <= e.event_time
      AND EXTRACT (epoch FROM (now() - interval '60 days')) <= m.access_granted_at
    GROUP BY e.member_id, mt.iso_year, mt.iso_week, ((e.event_time - m.access_granted_at) / (7 * 24 * 3600))
) a
GROUP BY access_granted_year, access_granted_week, activity_week
