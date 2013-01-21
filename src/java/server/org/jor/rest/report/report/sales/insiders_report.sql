SELECT m.email, to_timestamp(m.member_created_at) AS member_creation_date,
       to_timestamp(m.access_granted_at) AS insider_since,
       to_timestamp(MAX(e.event_time)) AS last_activity_at
FROM events e
     INNER JOIN member_dimension m ON (m.id = e.member_id AND m.has_private_projects = true)
     INNER JOIN project_dimension p ON (p.id = e.project_id AND p.is_private)
GROUP BY m.email, m.member_created_at, m.access_granted_at
