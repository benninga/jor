package org.jor.server.services.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class PersistenceManager
{
    private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);
    private static final Map<String, SessionFactory> globalSessionFactories = new HashMap<>();
    private static final Map<String, SessionFactory> customFactories = new HashMap<>();
    
    private String serviceName;
    private Session session;
    private boolean debugDialect;
    private Transaction currentTransaction;
    
    public PersistenceManager(String serviceName)
    {
        if (serviceName == null) {
            throw new NullPointerException("Service name cannot be null");
        }
        this.serviceName = serviceName;
        Dialect dialect = ((SessionFactoryImpl)getSessionFactory(serviceName)).getDialect();
        this.debugDialect = (dialect instanceof HSQLDialect);
        this.session = getSessionFactory(serviceName).openSession();
    }
    
    public boolean isDebugDialect()
    {
        return debugDialect;
    }
    
    public boolean isActiveTransaction()
    {
        return currentTransaction != null;
    }
    
    public void beginOrContinueTransaction()
    {
        if (currentTransaction == null)
        {
            currentTransaction = new Transaction(session);
            currentTransaction.begin();
        }
    }
    
    public void commitTransactionIfOpen()
    {
        if (currentTransaction != null)
        {
            try
            {
                currentTransaction.commit();
                currentTransaction = null;
            }
            catch (org.hibernate.exception.ConstraintViolationException e)
            {
                throw new ConstraintViolationException(e);
            }
        }
    }
    
    public void rollbackIfOpen()
    {
        if (currentTransaction != null)
        {
            currentTransaction.rollback();
            currentTransaction = null;
        }
    }
    
    public void close()
    {
        session.close();
        session = null;
    }
    
    public void flush()
    {
        session.flush();
    }
    
    public <T extends ObjectJDO> T makePersistent(T object)
    {
        try
        {
            ObjectKey key = object.getKey();
            Long id = key.getId();
            if(id != null)
            //if(session.get(object.getClass(), object.getKey().getId()) != null)
            {
                object = (T)session.merge(object);
            }
            session.saveOrUpdate(object);
            return object;
        }
        catch (TransientObjectException e)
        {
            throw new InvalidObjectException(e);
        }
        catch (PropertyValueException e)
        {
            throw new InvalidObjectException(e);
        }
        catch (NonUniqueObjectException e)
        {
            throw new ConstraintViolationException(e);
        }
        catch (org.hibernate.exception.ConstraintViolationException e)
        {
            throw new ConstraintViolationException(e);
        }
    }
    
    public <T extends ObjectJDO> void deletePersistent(T object)
    {
        Object merged = session.merge(object);
        session.delete(merged);
    }
    
    public <T extends ObjectJDO> T getObjectById(Class<T> clazz, ObjectKey key)
    {
        return getObjectById(clazz, key.getId());
    }
    
    public <T extends ObjectJDO> T getObjectById(Class<T> clazz, Serializable id)
    {
        return (T)session.load(clazz, id);
    }
    
    public <T extends ObjectJDO> SimplifiedQuery newQuery(Class<T> clazz)
    {
        return new SimplifiedQuery(session, clazz);
    }
    
    public <T extends ObjectJDO> SimplifiedQuery newQuery(Class<T> clazz, int maxRecords)
    {
        SimplifiedQuery query = new SimplifiedQuery(session, clazz);
        query.setMaxResults(maxRecords);
        return query;
    }
    
    public <T extends ObjectJDO> GenericQuery newQuery(String queryStr)
    {
        return new GenericQuery(session, queryStr);
    }
    
    public SQLQuery newSQLQuery(String queryStr)
    {
        return new SQLQuery(session, queryStr);
    }
    
    public SQLQuery newSQLQuery(String queryStr, String[] columnNames)
    {
        return new SQLQuery(session, queryStr, columnNames);
    }
    
    public void doWork(Work work)
    {
        session.doWork(work);
    }
    
    public ClassMetadata getMetadata(Class<?> clazz)
    {
        return getSessionFactory(serviceName).getClassMetadata(clazz);
    }
    
    protected static SessionFactory buildSessionFactory()
    {
        return buildSessionFactory(DataService.getDefaultDatabaseName());
    }
    
    protected static SessionFactory buildSessionFactory(String serviceName)
    {
        if (serviceName == null) {
            throw new NullPointerException("Service name cannot be null");
        }
        try
        {
            String file = getConfigurationFileName(serviceName);
            return new Configuration().configure(file).buildSessionFactory();
        }
        catch (Throwable ex)
        {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    private static String getConfigurationFileName(String serviceName)
    {
        final String BASE_NAME = "hibernate.cfg.xml";
        return "/" + serviceName + "-" + BASE_NAME;
    }
    
    protected static SessionFactory getSessionFactory()
    {
        return getSessionFactory(DataService.getDefaultDatabaseName());
    }
    
    protected static SessionFactory getSessionFactory(String serviceName)
    {
        SessionFactory sessionFactory = customFactories.get(serviceName);
        if(sessionFactory != null)
        {
            return sessionFactory;
        }
        
        SessionFactory globalSessionFactory = globalSessionFactories.get(serviceName);
        if (globalSessionFactory == null)
        {
            globalSessionFactory = buildSessionFactory(serviceName);
            globalSessionFactories.put(serviceName, globalSessionFactory);
        }
        return globalSessionFactory;
    }
    
    public static void setSessionFactory(SessionFactory factory)
    {
        setSessionFactory(DataService.getDefaultDatabaseName(), factory);
    }
    
    public static void setSessionFactory(String serviceName, SessionFactory factory)
    {
        customFactories.put(serviceName, factory);
    }
}
