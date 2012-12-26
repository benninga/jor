package org.jor.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class DataTable
{
    private static final String NEW_LINE = "\n";
    
    private List<String> headers;
    private List<Object[]> rows;
    
    public DataTable(String[] headers, List<Object[]> rows)
    {
        this.headers = Lists.newArrayList(headers);
        this.rows = rows;
    }
    
    public void addColumn(String header, Object[] columnRows)
    {
        this.addColumn(header, Lists.newArrayList(columnRows));
    }

    public void addColumn(String header, List<Object> columnRows)
    {
        if (this.rows.size() != columnRows.size()) {
            throw new RuntimeException(String.format("New column has %d rows but existing table has %d rows",
                                                     columnRows.size(), rows.size()));
        }
        
        headers.add(header);
        List<Object[]> newRows = new ArrayList<>(this.rows.size());
        for (int i = 0; i < this.rows.size(); i ++)
        {
            Object[] oldRow = rows.get(i);
            Object[] newRow = Arrays.copyOf(oldRow, oldRow.length + 1);
            newRow[oldRow.length] = columnRows.get(i);
            newRows.add(newRow);
        }
        this.rows = newRows;
    }
    
    public String toCsv()
    {
        StringBuilder b = new StringBuilder();
        String comma = "";
        for (String item: headers) {
            b.append(comma).append(item);
            comma = ",";
        }
        b.append(NEW_LINE);
        
        for (Object[] row : rows)
        {
            comma = "";
            for (Object item : row)
            {
                b.append(comma).append(item);
                comma = ",";
            }
            b.append(NEW_LINE);
        }
        return b.toString();
    }
}
