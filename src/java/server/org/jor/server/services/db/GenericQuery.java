package org.jor.server.services.db;

import org.hibernate.Session;

public class GenericQuery extends Query
{
    private String queryStr;
    
    protected GenericQuery(Session session, String queryStr)
    {
        super(session);
        this.queryStr = queryStr;
    }

    @Override
    protected String getQueryString()
    {
        return queryStr;
    }

}
