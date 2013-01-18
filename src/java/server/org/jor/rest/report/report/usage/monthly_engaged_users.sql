SELECT
       SUM ( CASE WHEN (jan_2012 = 6) THEN 1 ELSE 0 END) AS jan_2012,
       SUM ( CASE WHEN (feb_2012 = 6) THEN 1 ELSE 0 END) AS feb_2012,
       SUM ( CASE WHEN (mar_2012 = 6) THEN 1 ELSE 0 END) AS mar_2012,
       SUM ( CASE WHEN (apr_2012 = 6) THEN 1 ELSE 0 END) AS apr_2012,
       SUM ( CASE WHEN (may_2012 = 6) THEN 1 ELSE 0 END) AS may_2012,
       SUM ( CASE WHEN (jun_2012 = 6) THEN 1 ELSE 0 END) AS jun_2012,
       SUM ( CASE WHEN (jul_2012 = 6) THEN 1 ELSE 0 END) AS jul_2012,
       SUM ( CASE WHEN (aug_2012 = 6) THEN 1 ELSE 0 END) AS aug_2012,
       SUM ( CASE WHEN (sep_2012 = 6) THEN 1 ELSE 0 END) AS sep_2012,
       SUM ( CASE WHEN (oct_2012 = 6) THEN 1 ELSE 0 END) AS oct_2012,
       SUM ( CASE WHEN (nov_2012 = 6) THEN 1 ELSE 0 END) AS nov_2012,
       SUM ( CASE WHEN (dec_2012 = 6) THEN 1 ELSE 0 END) AS dec_2012,
       
       SUM ( CASE WHEN (jan_2013 = 6) THEN 1 ELSE 0 END) AS jan_2013,
       SUM ( CASE WHEN (feb_2013 = 6) THEN 1 ELSE 0 END) AS feb_2013,
       SUM ( CASE WHEN (mar_2013 = 6) THEN 1 ELSE 0 END) AS mar_2013,
       SUM ( CASE WHEN (apr_2013 = 6) THEN 1 ELSE 0 END) AS apr_2013,
       SUM ( CASE WHEN (may_2013 = 6) THEN 1 ELSE 0 END) AS may_2013,
       SUM ( CASE WHEN (jun_2013 = 6) THEN 1 ELSE 0 END) AS jun_2013,
       SUM ( CASE WHEN (jul_2013 = 6) THEN 1 ELSE 0 END) AS jul_2013,
       SUM ( CASE WHEN (aug_2013 = 6) THEN 1 ELSE 0 END) AS aug_2013,
       SUM ( CASE WHEN (sep_2013 = 6) THEN 1 ELSE 0 END) AS sep_2013,
       SUM ( CASE WHEN (oct_2013 = 6) THEN 1 ELSE 0 END) AS oct_2013,
       SUM ( CASE WHEN (nov_2013 = 6) THEN 1 ELSE 0 END) AS nov_2013,
       SUM ( CASE WHEN (dec_2013 = 6) THEN 1 ELSE 0 END) AS dec_2013
FROM
(
	SELECT member_id,
	       SUM( CASE WHEN (year = 2012 AND month BETWEEN 1 AND 1) OR (year = 2011 AND month BETWEEN 8 AND 12) THEN 1 ELSE 0 END) AS jan_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 1 AND 2) OR (year = 2011 AND month BETWEEN 9 AND 12) THEN 1 ELSE 0 END) AS feb_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 1 AND 3) OR (year = 2011 AND month BETWEEN 10 AND 12) THEN 1 ELSE 0 END) AS mar_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 1 AND 4) OR (year = 2011 AND month BETWEEN 11 AND 12) THEN 1 ELSE 0 END) AS apr_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 1 AND 5) OR (year = 2011 AND month BETWEEN 12 AND 12) THEN 1 ELSE 0 END) AS may_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 1 AND 6) THEN 1 ELSE 0 END) AS jun_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 2 AND 7) THEN 1 ELSE 0 END) AS jul_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 3 AND 8) THEN 1 ELSE 0 END) AS aug_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 4 AND 9) THEN 1 ELSE 0 END) AS sep_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 5 AND 10) THEN 1 ELSE 0 END) AS oct_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 6 AND 11) THEN 1 ELSE 0 END) AS nov_2012,
           SUM( CASE WHEN (year = 2012 AND month BETWEEN 7 AND 12) THEN 1 ELSE 0 END) AS dec_2012,
           
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 1 AND 1) OR (year = 2012 AND month BETWEEN 8 AND 12) THEN 1 ELSE 0 END) AS jan_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 1 AND 2) OR (year = 2012 AND month BETWEEN 9 AND 12) THEN 1 ELSE 0 END) AS feb_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 1 AND 3) OR (year = 2012 AND month BETWEEN 10 AND 12) THEN 1 ELSE 0 END) AS mar_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 1 AND 4) OR (year = 2012 AND month BETWEEN 11 AND 12) THEN 1 ELSE 0 END) AS apr_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 1 AND 5) OR (year = 2012 AND month BETWEEN 12 AND 12) THEN 1 ELSE 0 END) AS may_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 1 AND 6) THEN 1 ELSE 0 END) AS jun_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 2 AND 7) THEN 1 ELSE 0 END) AS jul_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 3 AND 8) THEN 1 ELSE 0 END) AS aug_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 4 AND 9) THEN 1 ELSE 0 END) AS sep_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 5 AND 10) THEN 1 ELSE 0 END) AS oct_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 6 AND 11) THEN 1 ELSE 0 END) AS nov_2013,
           SUM( CASE WHEN (year = 2013 AND month BETWEEN 7 AND 12) THEN 1 ELSE 0 END) AS dec_2013
	FROM
	(
		SELECT DISTINCT EXTRACT (year from created_at) AS year, EXTRACT (month from created_at) AS month, member_id
		FROM page_views
	) a
	GROUP BY member_id
) b
