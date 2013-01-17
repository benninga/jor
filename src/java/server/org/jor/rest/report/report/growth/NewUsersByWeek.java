package org.jor.rest.report.report.growth;

import java.util.List;

import org.jor.rest.report.report.BaseReport;
import org.jor.server.services.db.DataService;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class NewUsersByWeek extends BaseReport
{
    public NewUsersByWeek(Query query)
    {
        super(query);
    }

    @Override
    public DataTable getData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        String totalUsersSql = "SELECT COUNT(*) AS total_users FROM member_dimension";
        Object result = service.runSQLQuery(totalUsersSql).get(0);
        final Long totalCurrentUsers = ((Number)result).longValue();
        
        String sql =
                "SELECT t.week_of_year AS week, count(m.id) signups"
                + " FROM member_dimension m INNER JOIN time_dimension t ON (m.member_created_at = t.id)"
                + " WHERE EXTRACT (epoch FROM (now() - interval '140 days')) < m.member_created_at"
                + "   AND EXTRACT (week FROM now()) != t.week_of_year"
                + " GROUP BY t.week_of_year"
                + " ORDER BY t.week_of_year ASC";
        String[] headers = new String[] { "week", "signups" };
        List<Object[]> rows = service.runSQLQuery(sql, headers);

        // Setup the columns for the report
        addColumn("week", ValueType.NUMBER, "Week of Year");
        addColumn("signup_count", ValueType.NUMBER, "Signup Count");
        addColumn("preweek_total_count", ValueType.NUMBER, "PreWeek Total Count");
        addColumn("signup_percentage", ValueType.NUMBER, "Signup Percentage of Total");
        addColumn("signup_change", ValueType.NUMBER, "Percent (%) Change from Previous Week");
        
        if (rows.size() == 0) {
            return getData();
        }
        
        // Compute the total users prior available prior to each week
        Double[] percentRows = new Double[rows.size()];
        Long lastTotal = totalCurrentUsers;
        Long[] preWeekTotals = new Long[rows.size()];
        for (int i = rows.size() - 1; i >= 0; i --)
        {
            Number weeklySignups = (Number)rows.get(i)[1];
            Long preWeekTotal = lastTotal - weeklySignups.longValue();
            Double percentChange = (weeklySignups.doubleValue() / preWeekTotal.doubleValue()) * 100; 
            preWeekTotals[i] = preWeekTotal;
            percentRows[i] = percentChange;
            lastTotal = preWeekTotal;
        }
        
        // Compute the percent increase (percent of existing users)
        Double[] percentChangeRows = new Double[rows.size()];
        percentChangeRows[0] = 0D;
        double previousValue = percentRows[0];
        percentChangeRows[0] = 0D;
        for (int i = 1; i < rows.size(); i ++)
        {
            Double currentPercent = percentRows[i];
            percentChangeRows[i] = currentPercent - previousValue;
            previousValue = currentPercent;
        }

        // Fill the data table.
        for (int i = 0; i < rows.size(); i ++)
        {
            Object[] row = rows.get(i);
            addRow(row[0], row[1], preWeekTotals[i], percentRows[i], percentChangeRows[i]);
        }
        return getTable();
    }
}
