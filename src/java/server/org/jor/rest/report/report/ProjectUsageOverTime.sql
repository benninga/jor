-- Returns average operations, per week, per private project.
SELECT week || '-' || year,
       AVG(activity_count) AS avg_activity,
       AVG(file_downloaded) AS avg_downloads,
       AVG(viewer_opened) AS avg_3d_views,
       AVG(comment_added) + AVG(pin_comment_added) + AVG(file_comment_added) AS avg_comments
FROM
(
    SELECT project_id, year, week, COUNT(event_type_id) AS activity_count,
        SUM ( CASE WHEN (event_type_id = 0) THEN 1 ELSE 0 END) AS unknown,
        SUM ( CASE WHEN (event_type_id = 1) THEN 1 ELSE 0 END) AS project_created,
        SUM ( CASE WHEN (event_type_id = 2) THEN 1 ELSE 0 END) AS project_opened,
        SUM ( CASE WHEN (event_type_id = 3) THEN 1 ELSE 0 END) AS file_added,
        SUM ( CASE WHEN (event_type_id = 4) THEN 1 ELSE 0 END) AS file_downloaded,
        SUM ( CASE WHEN (event_type_id = 5) THEN 1 ELSE 0 END) AS file_deleted,
        SUM ( CASE WHEN (event_type_id = 6) THEN 1 ELSE 0 END) AS viewer_opened,
        SUM ( CASE WHEN (event_type_id = 7) THEN 1 ELSE 0 END) AS comment_added,
        SUM ( CASE WHEN (event_type_id = 8) THEN 1 ELSE 0 END) AS file_comment_added,
        SUM ( CASE WHEN (event_type_id = 9) THEN 1 ELSE 0 END) AS pin_comment_added,
        SUM ( CASE WHEN (event_type_id = 10) THEN 1 ELSE 0 END) AS project_owner_added,
        SUM ( CASE WHEN (event_type_id = 11) THEN 1 ELSE 0 END) AS collaborator_added,
        SUM ( CASE WHEN (event_type_id = 12) THEN 1 ELSE 0 END) AS limited_collaborator_added,
        SUM ( CASE WHEN (event_type_id = 13) THEN 1 ELSE 0 END) AS project_owner_deleted,
        SUM ( CASE WHEN (event_type_id = 14) THEN 1 ELSE 0 END) AS collaborator_deleted,
        SUM ( CASE WHEN (event_type_id = 15) THEN 1 ELSE 0 END) AS limited_collaborator_deleted,
        SUM ( CASE WHEN (event_type_id = 16) THEN 1 ELSE 0 END) AS badge_granted
    FROM
    (
      SELECT e.project_id, e.event_type_id,
             EXTRACT (ISOYEAR FROM to_timestamp(event_time)) AS year, EXTRACT (WEEK FROM to_timestamp(event_time)) AS week
      FROM events e
           INNER JOIN member_dimension m ON (e.member_id = m.id AND m.email NOT LIKE '%grabcad.com' AND m.has_private_projects = true)
           INNER JOIN project_dimension p ON (e.project_id = p.id AND p.is_private = true)
      WHERE e.event_time > extract (epoch FROM (now() - interval '70 days'))
    ) a
    GROUP BY project_id, year, week
) b
WHERE activity_count > 0
GROUP by year, week
ORDER BY year, week
