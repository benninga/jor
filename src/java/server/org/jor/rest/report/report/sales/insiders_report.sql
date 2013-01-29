SELECT m.email, to_timestamp(m.member_created_at) AS member_creation_date,
       to_timestamp(m.access_granted_at) AS insider_since,
       to_timestamp(MAX(e.event_time)) AS last_activity_at
FROM member_dimension m
     LEFT OUTER JOIN
     (
      SELECT e.event_time, e.member_id
      FROM events e INNER JOIN project_dimension p ON (p.id = e.project_id AND p.is_private)
     ) e ON (m.id = e.member_id AND m.has_private_projects = true)
WHERE m.has_private_projects = true
GROUP BY m.email, m.member_created_at, m.access_granted_at
