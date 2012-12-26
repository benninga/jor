package org.jor.rest.report.report;

import java.util.List;

import org.jor.server.services.db.DataService;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class ActivityTypeDistribution extends BaseReport
{
    public ActivityTypeDistribution(Query query)
    {
        super(query);
    }
    
    @Override
    public DataTable getData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        
        String sql = "SELECT etd.event_name AS event_name, COUNT(e.id) AS event_count" +
                     " FROM events e, event_type_dimension etd" +
                     " WHERE e.event_type_id = etd.id" +
                     " GROUP BY etd.event_name ORDER BY COUNT(e.id) DESC";

        List<Object[]> rows = service.runSQLQuery(sql);

        // Create a data table,
        addColumn("event_name", ValueType.TEXT, "Event Name");
        addColumn("event_count", ValueType.NUMBER, "Event Count");

        // Fill the data table.
        for (int i = 0; i < rows.size(); i ++)
        {
            Object[] row = rows.get(i);
            addRow(row[0], row[1]);
        }
        
        return getTable();
    }
}
