package org.jor.server.services.db;

import org.hibernate.Session;

public class SQLQuery extends Query
{
    private String queryStr;
    private String[] columnNames;
    
    protected SQLQuery(Session session, String queryStr)
    {
        this(session, queryStr, null);
    }
    
    protected SQLQuery(Session session, String queryStr, String[] columnNames)
    {
        super(session);
        this.queryStr = queryStr;
        this.columnNames = columnNames;
    }

    @Override
    protected String getQueryString()
    {
        return queryStr;
    }
    
    @Override
    protected org.hibernate.Query createQuery(String queryValue)
    {
        org.hibernate.SQLQuery query = getSession().createSQLQuery(queryValue);
        if (columnNames != null)
        {
            for (String columnAlias : columnNames)
            {
                query.addScalar(columnAlias);
            }
        }
        return query;
    }
}
