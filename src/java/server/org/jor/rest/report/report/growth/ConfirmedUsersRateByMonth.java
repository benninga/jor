package org.jor.rest.report.report.growth;

import java.util.List;

import org.jor.rest.report.report.BaseReport;
import org.jor.server.services.db.DataService;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class ConfirmedUsersRateByMonth extends BaseReport
{
    public ConfirmedUsersRateByMonth(Query query)
    {
        super(query);
    }

    @Override
    public DataTable getData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        
        String sql = getTextFile("confirmed_users_by_month.sql");
        List<Object[]> rows = service.runSQLQuery(sql);

        // Setup the columns for the report
        addColumn("month", ValueType.TEXT, "Month of Year");
        addColumn("signup_count", ValueType.NUMBER, "Signup Count");
        addColumn("confirmed_count", ValueType.NUMBER, "Confirmed Users Count");
        addColumn("unconfirmed_count", ValueType.NUMBER, "Unconfirmed Users Count");
        addColumn("confirmed_percentage", ValueType.NUMBER, "Confirmed Percentage");
        
        // Fill the data table.
        for (int i = 0; i < rows.size(); i ++)
        {
            Object[] row = rows.get(i);
            String monthName = row[0] + "_" + row[1];
            Number totalSignup = (Number)row[2];
            Number unconfirmed = (Number)row[3];
            Number confirmed = (Number)row[4];
            Number percentage = (confirmed.doubleValue() / totalSignup.doubleValue() * 100.0);
            addRow(monthName, totalSignup, confirmed, unconfirmed, percentage);
        }
        return getTable();
    }

}
