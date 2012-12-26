package org.jor.rest.report;

import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;

public interface ReportResource
{
    DataTable getData();
    DataTable getData(Query query);
    
}
