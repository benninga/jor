package org.jor.rest.report.report.activity;

import com.google.visualization.datasource.query.Query;

public class InsiderActivityWeeklyCohorts extends BaseActivityByWeeklyCohorts
{
    public InsiderActivityWeeklyCohorts(Query query)
    {
        super(query, "insider_access_granted_by_week.sql", "insider_activity_by_week.sql");
    }
}
