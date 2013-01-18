package org.jor.rest.report.report.sales;

import java.util.List;

import org.jor.rest.report.report.BaseReport;
import org.jor.server.services.db.DataService;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class InsidersReport extends BaseReport
{

    public InsidersReport(Query query)
    {
        super(query);
    }

    @Override
    public DataTable getData()
    {
        String sql = getTextFile("insiders_report.sql");
        DataService service = DataService.getDataService(METRICS_POSTGRES_DB);
        List<Object[]> data = service.runSQLQuery(sql);
        
        addColumn("email", ValueType.TEXT, "Email");
        addColumn("member_creation_date", ValueType.TEXT, "Member Creation Date");
        addColumn("insider_since", ValueType.TEXT, "Insider Granted Date");
        addColumn("last_activity_at", ValueType.TEXT, "Last Activity Date");
        
        for (Object[] row : data)
        {
            addRow(row[0], getDateTime(row[1]), getDateTime(row[2]), getDateTime(row[3]));
        }
        return getTable();
    }
    
    private Object getDateTime(Object value)
    {
        return value.toString();
    }
    
}
