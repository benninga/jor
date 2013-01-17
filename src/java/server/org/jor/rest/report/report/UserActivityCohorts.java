package org.jor.rest.report.report;

import com.google.visualization.datasource.query.Query;

public class UserActivityCohorts extends BaseActivityByWeeklyCohorts
{
    public UserActivityCohorts(Query query)
    {
        super(query, "user_created_by_week.sql", "user_activity_by_week.sql");
    }
}
