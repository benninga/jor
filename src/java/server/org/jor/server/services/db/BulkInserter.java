package org.jor.server.services.db;

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

public class BulkInserter
{
    private static Logger LOG = LoggerFactory.getLogger(BulkInserter.class);
    
    private static final int MAX_BUFFER = 1045000; // Max MySQL Packet size - (minus ~500 bytes for room) 
    
    private final Connection connection;
    private final String tableName;
    private final String insertQuery;
    private final String valuesTuple;
    private final MessageFormat formatter;
    private boolean logStatistics;
    
    private StringBuffer queryBuilder;
    private int currentCount;
    private int maxBulkCount;
    private int totalInserts;
    private String comma;
    
    public BulkInserter(String tableName, String insertPrefix,
                        String valuesTuple, Connection connection)
    {
        this(tableName, insertPrefix, valuesTuple, connection, Integer.MAX_VALUE,
             true);
    }
    
    public BulkInserter(String tableName, String insertPrefix, String valuesTuple,
                        Connection connection, int maxBulkCount, boolean logStatistics)
    {
        this.tableName = tableName;
        this.insertQuery = insertPrefix;
        this.valuesTuple = valuesTuple;
        this.connection = connection;
        this.formatter = new MessageFormat(this.valuesTuple);
        this.maxBulkCount = maxBulkCount;
        this.queryBuilder = new StringBuffer(MAX_BUFFER + 1000);
        this.logStatistics = logStatistics;
        this.totalInserts = 0;
        
        resetState();
        
        writeStartLog();
    }
    
    public void setLogStatistics(boolean log)
    {
        this.logStatistics = log;
    }
    
    public void insertTuple(Object[] values) throws SQLException
    {
        String[] strValues = new String[values.length];
        for (int i = 0; i < values.length; i ++)
        {
            Object obj = values[i];
            strValues[i] = (obj == null) ? null : obj.toString();
        }
        insertTuple(strValues);
    }
    
    public void insertTuple(String[] values) throws SQLException
    {
        queryBuilder.append(comma);
        comma = ", ";
        formatter.format(values, queryBuilder, null);
        currentCount ++;
        
        // We should compute the size before appending the next value.
        // Here, we may have already overflowed if the current values array is very large
        if (queryBuilder.length() >= MAX_BUFFER || currentCount >= maxBulkCount)
        {
            flush();
        }
    }
    
    public void close() throws SQLException
    {
        flush();
    }
    
    private void flush() throws SQLException
    {
        if (currentCount > 0)
        {
            String query = queryBuilder.toString();
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Running query:\n" + query);
                LOG.info("Flushing bulk insert query");
            }
            long startTime = System.currentTimeMillis();
            try
            {
                Statement statement = connection.createStatement();
                statement.execute(query);
            }
            catch (Exception e)
            {
                LOG.warn("Failed to execute query: : " + query);
                throw new SQLException("Failed to execute query: " + query, e);
            }
            if (logStatistics)
            {
                long timeInterval = System.currentTimeMillis() - startTime;
                double speed = ((double)(currentCount * 1000)) / (double)timeInterval;
                System.out.println(String.format("xxx,%d,%d", totalInserts, (int)speed));
            }
        }
        
        resetState();
    }
    
    private void resetState()
    {
        queryBuilder.delete(0, queryBuilder.length());
        totalInserts += currentCount;
        currentCount = 0;
        comma = "";
        
        queryBuilder.append(insertQuery);
    }
    
    private void writeStartLog()
    {
        if(logStatistics == false) {
            return;
        }
        System.out.println(String.format("xxx,%s, Size,Speed", tableName));
    }
    
}
