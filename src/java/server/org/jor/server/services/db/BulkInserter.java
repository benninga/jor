package org.jor.server.services.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import org.hibernate.jdbc.Work;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class BulkInserter
{
    private static Logger LOG = LoggerFactory.getLogger(BulkInserter.class);
    
    private static final int MAX_BUFFER = 1045000; // Max MySQL Packet size - (minus ~500 bytes for room) 
    
    private final QueryExecutor queryExecutor;
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
    
    public static interface QueryExecutor
    {
        void executeQuery(String sql) throws SQLException;
    }
    
    public BulkInserter(String tableName, String insertPrefix,
                        String valuesTuple, Connection connection)
    {
        this(tableName, insertPrefix, valuesTuple, new RawConnectionExecutor(connection));
    }
    
    public BulkInserter(String tableName, String insertPrefix,
                        String valuesTuple, DataService dataService)
    {
        this(tableName, insertPrefix, valuesTuple, new DataServiceExecutor(dataService));
    }
    
    public BulkInserter(String tableName, String insertPrefix,
                        String valuesTuple, QueryExecutor queryExecutor)
    {
        this(tableName, insertPrefix, valuesTuple, queryExecutor, Integer.MAX_VALUE, true);
    }
    
    public BulkInserter(String tableName, String insertPrefix, String valuesTuple,
                        QueryExecutor queryExecutor, int maxBulkCount, boolean logStatistics)
    {
        this.tableName = tableName;
        this.insertQuery = insertPrefix;
        this.valuesTuple = valuesTuple;
        this.queryExecutor = queryExecutor;
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
                queryExecutor.executeQuery(query);
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
    
    public static class DataServiceExecutor implements QueryExecutor
    {
        private DataService service;
        
        public DataServiceExecutor(DataService service)
        {
            this.service = service;
        }
        
        @Override
        public void executeQuery(final String sql)
        {
            service.doWork(new Work()
            {
                @Override public void execute(Connection connection) throws SQLException
                {
                    try (Statement statement = connection.createStatement();)
                    {
                        statement.execute(sql);
                    }
                }
            });
        }
        
    }
    
    public static class RawConnectionExecutor implements QueryExecutor
    {
        private Connection connection;
        
        public RawConnectionExecutor(Connection connection)
        {
            this.connection = connection;
        }
        
        @Override
        public void executeQuery(String sql) throws SQLException
        {
            try (Statement statement = connection.createStatement();)
            {
                statement.execute(sql);
            }
        }
    }
    
}
