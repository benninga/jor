package org.jor.rest.report.report.usage;

import java.util.Objects;

public class MonthlyCohortInfo implements Comparable<MonthlyCohortInfo>
{
    private final String cohortName;
    private final Integer year;
    private final Integer month;
    private final int totalUsers;
    private final int newUsers;
    public Object[] stats;
    
    public MonthlyCohortInfo(int year, int month, int totalUsers, int newUsers, int statsSize)
    {
        this.year = year;
        this.month = month;
        this.cohortName = cohortName(year, month);
        this.totalUsers = totalUsers;
        this.newUsers = newUsers;
        this.stats = new Object[statsSize];
        
        Objects.requireNonNull(cohortName, "Cohorts name cannot be null");
    }
    
    public int getTotalUsers()
    {
        return totalUsers;
    }
    
    public Object[] getData()
    {
        Object[] data = new Object[3 + stats.length];
        data[0] = cohortName;
        data[1] = totalUsers;
        data[2] = newUsers;
        for (int i = 0; i < stats.length; i ++)
        {
            data[3 + i] = stats[i];
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
        if (other == null || (other instanceof MonthlyCohortInfo) == false) {
            return false;
        }
        MonthlyCohortInfo otherCohort = (MonthlyCohortInfo)other;
        return cohortName.equals(otherCohort.cohortName);
    }

    @Override
    public int compareTo(MonthlyCohortInfo o)
    {
        if (year.equals(o.year)) {
            return month.compareTo(o.month);
        }
        return year.compareTo(o.year);
    }
    
    protected static String cohortName(int isoYear, int isoWeek)
    {
        return isoWeek + "_" + isoYear;
    }
}
