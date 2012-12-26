package org.jor.rest.report.report;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.jor.server.services.db.DataService;

import com.google.common.collect.Maps;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class UserActivityCohorts extends BaseReport
{
    private String userCohortsSql =
              "SELECT (member_week || '_' || member_year) AS user_create_week, activity_week, COUNT(member_id) AS user_count"
            + " FROM"
            + " ("
            + "     SELECT DISTINCT e.member_id AS member_id, mt.year AS member_year, mt.day_of_year / 7 AS member_week,"
            + "            (et.day_of_year - mt.day_of_year + ((et.year - mt.year) * 365)) / 7 AS activity_week"
            + "     FROM events e"
            + "          INNER JOIN time_dimension et ON e.event_time = et.id"
            + "          INNER JOIN time_dimension mt ON e.member_created_at = mt.id"
            + "     WHERE EXTRACT (epoch FROM (now() - interval '60 days')) <= e.event_time"
            + "       AND EXTRACT (epoch FROM (now() - interval '60 days')) <= e.member_created_at"
            + " ) a"
            + " GROUP BY member_year, member_week, activity_week";
    
    private String userCreatedSql =
              " SELECT (member_week || '_' || member_year) AS user_create_week, COUNT(member_id) AS user_count"
            + " FROM"
            + " ("
            + "     SELECT m.id AS member_id, t.year AS member_year, t.day_of_year / 7 AS member_week"
            + "     FROM member_dimension m INNER JOIN time_dimension t ON (m.member_created_at = t.id)"
            + "     WHERE EXTRACT (epoch FROM (now() - interval '60 days')) < m.member_created_at"
            + " ) a"
            + " GROUP BY member_year, member_week";
    
    private Map<String, CohortInfo> dataMap;
    
    public UserActivityCohorts(Query query)
    {
        super(query);
        dataMap = Maps.newHashMap();
    }

    @Override
    public DataTable getData()
    {
        getUserCreatedData();
        getCohortsActivityData();
        
        updateDataTable();
        
        return getTable();
    }
    
    private void getUserCreatedData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        
        // Get the total users created by week (our cohorts baseline)
        List<Object[]> userCreation = service.runSQLQuery(userCreatedSql);
        for (Object[] row : userCreation)
        {
            String cohortName = (String)row[0];
            int cohortSize = ((Number)row[1]).intValue();
            
            CohortInfo info = dataMap.get(cohortName);
            if (info != null) {
                throw new RuntimeException("Should not have seen this cohort yet: " + cohortName);
            }
            info = new CohortInfo(cohortName, cohortSize);
            dataMap.put(cohortName, info);
        }
    }
    
    private void getCohortsActivityData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        
        // Get the activity by week for cohorts
        List<Object[]> activityRows = service.runSQLQuery(userCohortsSql);
        for (Object[] row : activityRows)
        {
            String cohortName = (String)row[0];
            int activityWeek = ((Number)row[1]).intValue();
            int activeUserCount = ((Number)row[2]).intValue();
            
            CohortInfo info = dataMap.get(cohortName);
            Objects.requireNonNull(info, "Cohort should not be null. Check data. " + cohortName);
            
            switch (activityWeek)
            {
                case 0:
                    info.weeklyTotals[0] += activeUserCount;
                    break;
                case 1:
                    info.weeklyTotals[1] += activeUserCount;
                    break;
                case 2:
                    info.weeklyTotals[2] += activeUserCount;
                    break;
                case 3:
                    info.weeklyTotals[3] += activeUserCount;
                    break;
                case 4:
                    info.weeklyTotals[4] += activeUserCount;
                    break;
                case 5:
                    info.weeklyTotals[5] += activeUserCount;
                    break;
                case 6:
                    info.weeklyTotals[6] += activeUserCount;
                    break;
                case 7:
                    info.weeklyTotals[7] += activeUserCount;
                    break;
                default:
                    // Ignore default. We may have gotten the query math not perfectly and have older weeks
            }
        }
    }

    private void updateDataTable()
    {
        addColumn("cohort_name", ValueType.TEXT, "Cohort Name"); // 0
        addColumn("total_users", ValueType.NUMBER, "Total Users"); // 1
        addColumn("week_0",  ValueType.NUMBER, "Week 0"); // 2
        addColumn("week_0p", ValueType.NUMBER, "Week 0 %"); // 3
        addColumn("week_1",  ValueType.NUMBER, "Week 1"); // 4
        addColumn("week_1p", ValueType.NUMBER, "Week 1 %"); // 5
        addColumn("week_2",  ValueType.NUMBER, "Week 2"); // 6
        addColumn("week_2p", ValueType.NUMBER, "Week 2 %"); // 7
        addColumn("week_3",  ValueType.NUMBER, "Week 3"); // 8
        addColumn("week_3p", ValueType.NUMBER, "Week 3 %"); // 9
        addColumn("week_4",  ValueType.NUMBER, "Week 4"); // 10
        addColumn("week_4p", ValueType.NUMBER, "Week 4 %"); // 11
        addColumn("week_5",  ValueType.NUMBER, "Week 5"); // 12
        addColumn("week_5p", ValueType.NUMBER, "Week 5 %"); // 13
        addColumn("week_6",  ValueType.NUMBER, "Week 6"); // 14
        addColumn("week_6p", ValueType.NUMBER, "Week 6 %"); // 15
        addColumn("week_7",  ValueType.NUMBER, "Week 7"); // 16
        addColumn("week_7p", ValueType.NUMBER, "Week 7 %"); // 17
        
        Map<String, CohortInfo> sorted = new TreeMap<>(dataMap);
        
        for (CohortInfo info : sorted.values())
        {
            addRow(info.getData());
        }
    }
    
    private static class CohortInfo implements Comparable<CohortInfo>
    {
        private static final int WEEK_COUNT = 8;
        private String cohortName;
        private int totalUsers;
        private int[] weeklyTotals = new int[WEEK_COUNT];
        private int[] weeklyPercentages = new int[WEEK_COUNT];
        
        public CohortInfo(String cohortName, int totalUsers)
        {
            this.cohortName = cohortName;
            this.totalUsers = totalUsers;
            
            Objects.requireNonNull(cohortName, "Cohorts name cannot be null");
        }
        
        public Object[] getData()
        {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            Object[] data = new Object[2 + weeklyTotals.length + weeklyPercentages.length];
            data[0] = cohortName;
            data[1] = totalUsers;
            for (int i = 0; i < WEEK_COUNT; i ++)
            {
                int weeklyTotal = weeklyTotals[i];
                data[2 + (i * 2)] = weeklyTotal;
                double weeklyPercentage = Double.valueOf(twoDForm.format(100d * weeklyTotal  /totalUsers));
                data[2 + (i * 2) + 1] = weeklyPercentage;
            }
            return data;
        }
        
        @Override
        public int hashCode()
        {
            return cohortName.hashCode();
        }
        
        @Override
        public boolean equals(Object other)
        {
            if (other == null || (other instanceof CohortInfo) == false) {
                return false;
            }
            CohortInfo otherCohort = (CohortInfo)other;
            return cohortName.equals(otherCohort.cohortName);
        }

        @Override
        public int compareTo(CohortInfo o)
        {
            return cohortName.compareTo(o.cohortName);
        }
    }
}
