package org.jor.rest.report.report.usage;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jor.rest.report.report.BaseReport;
import org.jor.server.services.db.DataService;

import com.google.common.collect.Maps;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class MonthlyActiveUsers extends BaseReport
{

    private Map<String, MonthlyCohortInfo> dataMap;
    
    public MonthlyActiveUsers(Query query)
    {
        super(query);
        dataMap = Maps.newHashMap();
    }
    
    @Override
    public DataTable getData()
    {
        getUserCreatedData();
        getCohortsActivityData();
        getCohortsEngagedData();
        
        updateDataTable();
        
        return getTable();
    }

    private void updateDataTable()
    {
        addColumn("month_name", ValueType.TEXT, "Month of Year");
        addColumn("total_users", ValueType.NUMBER, "Total Users");
        addColumn("new_users", ValueType.NUMBER, "New Users");
        addColumn("active_users", ValueType.NUMBER, "Active Users");
        addColumn("active_users_percentage", ValueType.NUMBER, "Active Users Percentage");
        addColumn("engaged_users", ValueType.NUMBER, "Engaged Users");
        addColumn("engaged_users_percentage", ValueType.NUMBER, "Engaged Users Percentage");
        
        Set<MonthlyCohortInfo> sorted = new TreeSet<>(dataMap.values());
        
        for (MonthlyCohortInfo info : sorted)
        {
            addRow(info.getData());
        }
    }
    
    private void getUserCreatedData()
    {
        String userCreatedSql = getTextFile("user_counts_by_month.sql");
        
        DataService service = DataService.getDataService(PROD_POSTGRES_DB);
        
        // Get the total users created by week (our cohorts baseline)
        List<Object[]> userCreation = service.runSQLQuery(userCreatedSql);
        int totalUsers = 0;
        for (Object[] row : userCreation)
        {
            int year = ((Number)row[0]).intValue();
            int month = ((Number)row[1]).intValue();
            int cohortSize = ((Number)row[2]).intValue();
            totalUsers += cohortSize;
            
            String cohortName = MonthlyCohortInfo.cohortName(year, month);
            MonthlyCohortInfo info = dataMap.get(cohortName);
            if (info != null) {
                throw new RuntimeException("Should not have seen this cohort yet: " + cohortName);
            }
            info = new MonthlyCohortInfo(year, month, totalUsers, cohortSize, 4);
            dataMap.put(cohortName, info);
        }
    }
    
    private void getCohortsActivityData()
    {
        String activeUsersSql = getTextFile("monthly_active_users.sql");
        
        DataService service = DataService.getDataService(PROD_POSTGRES_DB);
        
        // Get the activity by cohort
        List<Object[]> activityRows = service.runSQLQuery(activeUsersSql);
        for (Object[] row : activityRows)
        {
            int actionYear = ((Number)row[0]).intValue();
            int actionMonth = ((Number)row[1]).intValue();
            int activeUsersCount = ((Number)row[2]).intValue();
            
            String cohortName = MonthlyCohortInfo.cohortName(actionYear, actionMonth);
            
            MonthlyCohortInfo info = dataMap.get(cohortName);
            Objects.requireNonNull(info, "Cohort should not be null. Check data. " + cohortName);
            
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            double activePercent = Double.valueOf(twoDForm.format(100d * activeUsersCount  / info.getTotalUsers()));
            
            info.stats[0] = activeUsersCount;
            info.stats[1] = activePercent;
        }
    }
    
    private void getCohortsEngagedData()
    {
        String activeUsersSql = getTextFile("monthly_engaged_users.sql");
        
        DataService service = DataService.getDataService(PROD_POSTGRES_DB);
        
        // Get the engaged counts by month
        // The query returns just one row with columns for Jan/2012 to Dec 2013
        List<Object[]> engagedRows = service.runSQLQuery(activeUsersSql);
        if (engagedRows.size() != 1) {
            throw new RuntimeException("Expected only one row for engaged users but received: " + engagedRows.size());
        }
        
        Object[] row = engagedRows.get(0);
        for (int i = 0; i < row.length; i ++)
        {
            int engagementYear = 2012 + (i / 12);
            int engagementMonth = 1 + (i % 12);
            int engagementCount = ((Number)row[i]).intValue();
            
            String cohortName = MonthlyCohortInfo.cohortName(engagementYear, engagementMonth);
            
            MonthlyCohortInfo info = dataMap.get(cohortName);
            if (info == null) {
                continue; // We may have some 0 for future dates.
            }
            
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            double engagementPercent = Double.valueOf(twoDForm.format(100d * engagementCount  / info.getTotalUsers()));
            
            info.stats[2] = engagementCount;
            info.stats[3] = engagementPercent;
        }
    }

}
