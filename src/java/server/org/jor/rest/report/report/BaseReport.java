package org.jor.rest.report.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

abstract public class BaseReport
{
    protected static final String PROD_POSTGRES_DB = "prod-postgres";
    protected static final String METRICS_POSTGRES_DB = "metrics-postgres";
    
    private Query query;
    private DataTable table;
    
    public BaseReport(Query query)
    {
        this.query = query;
        this.table = new DataTable();
    }
    
    protected Query getQuery()
    {
        return query;
    }
    
    protected DataTable getTable()
    {
        return table;
    }
    
    protected void addColumn(String id, ValueType type)
    {
        addColumn(id, type, id);
    }
    
    protected void addColumn(String id, ValueType type, String label)
    {
        table.addColumn(new ColumnDescription(id, type, label));
    }
    
    protected void addRow(Object ... values)
    {
        try
        {
            table.addRowFromValues(values);
        }
        catch (TypeMismatchException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public abstract DataTable getData();
    
    protected String getTextFile(String simpleName)
    {
        try (
                InputStream in = getClass().getResourceAsStream(simpleName);
             )
        {
            if (in == null) {
                throw new RuntimeException("Could not find resource: " + simpleName);
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder b = new StringBuilder();
            String line;
            while ( (line = reader.readLine()) != null)
            {
                b.append(line);
                b.append("\n");
            }
            return b.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to open text file. ", e);
        }
    }
}
