package org.jor.rest.report.report;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

abstract public class BaseReport
{
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
}
