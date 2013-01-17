package org.jor.rest.report.report;

import java.util.List;

import org.jor.server.services.db.DataService;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

public class InsiderActivityLast14Days extends BaseReport
{
    public InsiderActivityLast14Days(Query query)
    {
        super(query);
    }
    
    @Override
    public DataTable getData()
    {
        DataService service = DataService.getDataService("metrics-postgres");
        
        String sql = getTextFile("insider_activity_last_2_weeks.sql");

        List<Object[]> rows = service.runSQLQuery(sql);

        // Create a data table,
        addColumn("email", ValueType.TEXT, "Email");
        addColumn("activity_count", ValueType.NUMBER, "Activity Count");
        addColumn("unknown", ValueType.NUMBER, "unknown");
        addColumn("project_created", ValueType.NUMBER, "project_created");
        addColumn("project_opened", ValueType.NUMBER, "project_opened");
        addColumn("file_added", ValueType.NUMBER, "file_added");
        addColumn("file_downloaded", ValueType.NUMBER, "file_downloaded");
        addColumn("file_deleted", ValueType.NUMBER, "file_deleted");
        addColumn("viewer_opened", ValueType.NUMBER, "viewer_opened");
        addColumn("comment_added", ValueType.NUMBER, "comment_added");
        addColumn("file_comment_added", ValueType.NUMBER, "file_comment_added");
        addColumn("pin_comment_added", ValueType.NUMBER, "pin_comment_added");
        addColumn("project_owner_added", ValueType.NUMBER, "project_owner_added");
        addColumn("collaborator_added", ValueType.NUMBER, "collaborator_added");
        addColumn("limited_collaborator_added", ValueType.NUMBER, "limited_collaborator_added");
        addColumn("project_owner_deleted", ValueType.NUMBER, "project_owner_deleted");
        addColumn("collaborator_deleted", ValueType.NUMBER, "collaborator_deleted");
        addColumn("limited_collaborator_deleted", ValueType.NUMBER, "limited_collaborator_deleted");

        // Fill the data table.
        for (int i = 0; i < rows.size(); i ++)
        {
            Object[] row = rows.get(i);
            addRow(row);
        }
        
        return getTable();
    }
}
