package org.jor.rest.report.report;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jor.server.services.db.DataService;

import com.google.common.collect.Maps;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class UserActivityCohorts extends BaseReport
{
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
        String userCreatedSql = getTextFile("user_created_by_week.sql");
        
        DataService service = DataService.getDataService("metrics-postgres");
        
        // Get the total users created by week (our cohorts baseline)
        List<Object[]> userCreation = service.runSQLQuery(userCreatedSql);
        for (Object[] row : userCreation)
        {
            int isoYear = ((Number)row[0]).intValue();
            int isoWeek = ((Number)row[1]).intValue();
            int cohortSize = ((Number)row[2]).intValue();
            
            String cohortName = CohortInfo.cohortName(isoYear, isoWeek);
            CohortInfo info = dataMap.get(cohortName);
            if (info != null) {
                throw new RuntimeException("Should not have seen this cohort yet: " + cohortName);
            }
            info = new CohortInfo(isoYear, isoWeek, cohortSize);
            dataMap.put(cohortName, info);
        }
    }
    
    private void getCohortsActivityData()
    {
        String userCohortsSql = getTextFile("user_activity_by_week.sql");
        
        DataService service = DataService.getDataService("metrics-postgres");
        
        // Get the activity by week for cohorts
        List<Object[]> activityRows = service.runSQLQuery(userCohortsSql);
        for (Object[] row : activityRows)
        {
            int creationIsoYear = ((Number)row[0]).intValue();
            int creationIsoWeek = ((Number)row[1]).intValue();
            int activityWeek = ((Number)row[2]).intValue();
            int activeUserCount = ((Number)row[3]).intValue();
            String cohortName = CohortInfo.cohortName(creationIsoYear, creationIsoWeek);
            
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
        
        Set<CohortInfo> sorted = new TreeSet<>(dataMap.values());
        
        for (CohortInfo info : sorted)
        {
            addRow(info.getData());
        }
    }
    
    private static class CohortInfo implements Comparable<CohortInfo>
    {
        private static final int WEEK_COUNT = 8;
        private final String cohortName;
        private final Integer isoYear; // Year based on ISO 8601
        private final Integer isoWeek; // Week of year based on ISO 8601
        private final int totalUsers;
        private int[] weeklyTotals = new int[WEEK_COUNT];
        private int[] weeklyPercentages = new int[WEEK_COUNT];
        
        public CohortInfo(int isoYear, int isoWeek, int totalUsers)
        {
            this.isoYear = isoYear;
            this.isoWeek = isoWeek;
            this.cohortName = cohortName(isoYear, isoWeek);
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
            if (isoYear.equals(o.isoYear)) {
                return isoWeek.compareTo(o.isoWeek);
            }
            return isoYear.compareTo(o.isoYear);
        }
        
        protected static String cohortName(int isoYear, int isoWeek)
        {
            return isoWeek + "_" + isoYear;
        }
    }
}
