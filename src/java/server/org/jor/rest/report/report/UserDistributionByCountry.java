package org.jor.rest.report.report;

import java.util.List;

import org.jor.server.services.db.DataService;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class UserDistributionByCountry extends BaseReport
{

    public UserDistributionByCountry(Query query)
    {
        super(query);
    }

    @Override
    public DataTable getData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        String sql =
                "SELECT country AS Country, count(id) Popularity"
                + " FROM members GROUP BY country";
        String[] headers = new String[] { "Country", "Popularity" };
        List<Object[]> rows = service.runSQLQuery(sql, headers);
        
        // Create a data table,
        addColumn("Country", ValueType.TEXT, "Country");
        addColumn("Popularity", ValueType.NUMBER, "Popularity");

        // Fill the data table.
        for (int i = 0; i < rows.size(); i ++)
        {
            Object[] row = rows.get(i);
            addRow(row[0], row[1]);
        }
        return getTable();
    }

}
