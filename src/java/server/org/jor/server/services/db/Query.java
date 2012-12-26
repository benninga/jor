package org.jor.server.services.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.JDBCException;
import org.hibernate.Session;

abstract class Query
{
    private Session session;
    private Integer firstResult = null;
    private Integer maxResults = null;
    
    protected Query(Session session)
    {
        this.session = session;
    }

    protected abstract String getQueryString();
    
    protected final Session getSession()
    {
        return session;
    }
    
    public <T> List<T> execute()
    {
        Map<String, Object> parameterValues = new HashMap<>();
        return execute(parameterValues);
    }
    
    public <T> List<T> execute(String parameterName, Object parameterValue)
    {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put(parameterName, parameterValue);
        return execute(parameterValues);
    }
    
    public <T> List<T> execute(Map<String, Object> parameterValues)
    {
        try
        {
            org.hibernate.Query query = prepareQuery(parameterValues);
            if (firstResult != null) {
                query.setFirstResult(firstResult);
            }
            if (maxResults != null) {
                query.setMaxResults(maxResults);
            }
            
            List<T> result = query.list();
            if (result == null) {
                result = new ArrayList<>(0);
            }
            return result;
        }
        catch (JDBCException e)
        {
            throw new RuntimeException("Failed to execute query: " + getQueryString(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getUniqueResult(String parameterName, Object parameterValue)
    {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put(parameterName, parameterValue);
        return (T)getUniqueResult(parameterValues);
    }
    
    public Object getUniqueResult(Map<String, Object> parameterValues)
    {
        org.hibernate.Query query = prepareQuery(parameterValues);
        return query.uniqueResult();
    }
    
    protected org.hibernate.Query createQuery(String queryStr)
    {
        return session.createQuery(queryStr);
    }
    
    private org.hibernate.Query prepareQuery(Map<String, Object> parameterValues)
    {
        String queryStr = getQueryString();
        
        org.hibernate.Query query = createQuery(queryStr);
        if (parameterValues != null)
        {
            for(Map.Entry<String, Object> entry : parameterValues.entrySet())
            {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query;
    }
    
    public Integer getFirstResult()
    {
        return firstResult;
    }

    public void setFirstResult(Integer firstResult)
    {
        this.firstResult = firstResult;
    }

    public Integer getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults)
    {
        this.maxResults = maxResults;
    }
}
