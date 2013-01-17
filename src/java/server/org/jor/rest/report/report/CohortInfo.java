package org.jor.rest.report.report;

import java.text.DecimalFormat;
import java.util.Objects;

public class CohortInfo implements Comparable<CohortInfo>
{
    private static final int WEEK_COUNT = 8;
    private final String cohortName;
    private final Integer isoYear; // Year based on ISO 8601
    private final Integer isoWeek; // Week of year based on ISO 8601
    private final int totalUsers;
    public int[] weeklyTotals = new int[WEEK_COUNT];
    public int[] weeklyPercentages = new int[WEEK_COUNT];
    
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
