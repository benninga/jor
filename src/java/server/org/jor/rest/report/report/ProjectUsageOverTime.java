package org.jor.rest.report.report;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;

public class ProjectUsageOverTime extends BaseReport
{
    public ProjectUsageOverTime(Query query)
    {
        super(query);
    }

    @Override
    public DataTable getData()
    {
        String sql = getTextFile("project_usage_over_time.sql");
        System.out.println("SQL is: \n" + sql);
        return null;
    }
    
    public static void main(String args[]) 
    {
        ProjectUsageOverTime p = new ProjectUsageOverTime(null);
        p.getData();
    }
}
