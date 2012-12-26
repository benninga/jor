package org.jor.server.services.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.metadata.ClassMetadata;

import org.jor.server.services.SessionManager;
import org.jor.server.services.db.SimplifiedQuery.Filter;
import org.jor.server.services.db.SimplifiedQuery.OP;
import org.jor.server.services.db.SimplifiedQuery.Param;
import org.jor.server.services.user.SecurityContext;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class DataService
{
    
    private static final String P1 = "p1";
    private static final Logger LOG = LoggerFactory.getLogger(DataService.class);
    /**
     * The JDO persistence manager used for all calls.
     */
    private static PersistenceManagerFactory globalFactory = PersistenceManagerFactory.getPersistenceManagerFactory();
    private static final Map<String, DataService> namedSources = new HashMap<>();
    
    private final String serviceName;
    private final ThreadLocal<PersistenceManager> manager = new ThreadLocal<>();
    private static String defaultDbName = null;
    
    public static void setDefaultDatabaseName(String defaultName)
    {
        DataService.defaultDbName = defaultName;
    }
    
    public static String getDefaultDatabaseName()
    {
        return DataService.defaultDbName;
    }

    public static DataService getDataService()
    {
        return getDataService(DataService.defaultDbName);
    }
    
    public static Set<String> getNamedSources()
    {
    	return namedSources.keySet();
    }
    
    public static DataService getDataService(String name)
    {
        DataService service = namedSources.get(name);
        if (service == null)
        {
            service = new DataService(name);
            namedSources.put(name, service);
        }
        return service;
    }

    private DataService()
    {
        // Make sure constructor is not called except through this class.
        this(null);
    }
    
    private DataService(String name)
    {
        this.serviceName = name;
    }

    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager currentManager = manager.get();
        return currentManager;
    }
    
    public boolean isDebugDatabase()
    {
        return getPersistenceManager().isDebugDialect();
    }
    
    /**
     * Begin a new transaction or continue if one exists.
     * 
     * @return the transaction
     */
    public void begin()
    {
        PersistenceManager currentManager = getPersistenceManager();
        currentManager.beginOrContinueTransaction();
    }
    
    public void commit()
    {
        PersistenceManager currentManager = getPersistenceManager();
        currentManager.commitTransactionIfOpen();
    }

    public void rollback()
    {
        PersistenceManager currentManager = getPersistenceManager();
        currentManager.rollbackIfOpen();
        // Session may be in a bad state. We have to close it and open a new one.
        closeSession();
        openSession();
    }

    /**
     * Open a session to the data store for the calling thread.
     * Clients are expected to guarantee that close will be called.
     * This will also roll-back any active transaction.
     * 
     * In most common usage, this is handled at the HTTP Servlet level.
     * @see DataLayerThreadFilter
     */
    public void openSession()
    {
        PersistenceManager currentManager = getPersistenceManager();
        if (isSessionOpen())
        {
            throw new RuntimeException("Cannot open new session, a session already exists");
        }
        currentManager = globalFactory.getPersistenceManager(serviceName);
        manager.set(currentManager);
    }
    
    public boolean isSessionOpen()
    {
        PersistenceManager currentManager = getPersistenceManager();
        return (currentManager != null);
    }
    
    /**
     * Close the session to the data store for the calling thread.
     * Clients are expected to guarantee that close will be called.
     * This will also roll-back any active transaction.
     * 
     * In most common usage, this is handled at the HTTP Servlet level.
     * @see DataLayerThreadFilter
     */
    public void closeSession()
    {
        PersistenceManager currentManager = getPersistenceManager();
        if(currentManager == null)
        {
            return;
        }
        try
        {
            currentManager.rollbackIfOpen();
        }
        catch (Exception e) {
            LOG.error("Failed to rollback transaction", e);
        }
        currentManager.close();
        manager.remove();
    }
    
    public void flushSession()
    {
        PersistenceManager currentManager = getPersistenceManager();
        currentManager.flush();
    }
    
    private <T extends ObjectJDO> boolean isNew(T object)
    {
        return object.getId() == null;
    }
    
    public <T extends ObjectJDO> T save(T object)
            throws InvalidObjectException
    {
        return save(object, true);
    }
    
    public <T extends ObjectJDO> T saveUnverified(T object)
            throws InvalidObjectException
    {
        return save(object, false);
    }

    public <T extends ObjectJDO> T save(T object, Boolean verifyObject)
        throws InvalidObjectException
    {
        PersistenceManager currentManager = getPersistenceManager();
        
        boolean initiateTransaction = currentManager.isActiveTransaction() == false;
        currentManager.beginOrContinueTransaction();

        if (isNew(object) && object.getCreatedBy() == null)
        {
            SecurityContext sc = SessionManager.get().getSession().getSecurityContext();
            Long id = ObjectKey.convert(sc.getEffectiveUserKey()).getId();
            object.setCreatedBy(id);
        }
        object.setLastUpdateAt(new Date());
        
        // Check for object level verification (usually semantics).
        if (verifyObject)
        {
            String error2 = object.verifyObject();
            if (error2 != null)
            {
                throw new InvalidObjectException(error2);
            }
        }
        
        T updated = currentManager.makePersistent(object);

        if (initiateTransaction)
        {
            currentManager.commitTransactionIfOpen();
        }

        return updated;
    }

    public <T extends ObjectJDO> T getObject(Class<T> clazz, String objectKey)
    {
        return getObject(clazz, ObjectKey.convert(objectKey));
    }

    public <T extends ObjectJDO> T getObject(Class<T> clazz, ObjectKey objectKey)
    {
        PersistenceManager currentManager = getPersistenceManager();
        try
        {
            T object = currentManager.getObjectById(clazz, objectKey);
            // We need to invoke some method on the object to trigger the Hibernate not found exception
            object.toString();
            return object;
        }
        catch (ObjectNotFoundException e)
        {
            throw new JdoObjectNotFoundException("No object found for object key: " + objectKey, e);
        }
    }

    public <T extends ObjectJDO> void delete(T object)
        throws InvalidObjectException
    {
        PersistenceManager currentManager = getPersistenceManager();
        
        boolean initiateTransaction = currentManager.isActiveTransaction() == false;
        currentManager.beginOrContinueTransaction();
        
        currentManager.deletePersistent(object);

        if (initiateTransaction)
        {
            currentManager.commitTransactionIfOpen();
        }
    }
    
    public <T extends ObjectJDO> T getSingleObject(Class<T> clazz,
                                                   String columnName, Object parameterValue)
    {
        Filter filter = new Filter(columnName, OP.EQ, new Param(P1));
        return getSingleObject(clazz, filter, P1, parameterValue);
        
    }
    
    public <T extends ObjectJDO> T getSingleObject(Class<T> clazz, Filter filter,
                                                   String parameterName, Object parameterValue)
    {
        PersistenceManager currentManager = getPersistenceManager();
        if (currentManager == null)
        {
            throw new NullPointerException("Did you forget to initialize the data service?");
        }
        SimplifiedQuery query = currentManager.newQuery(clazz);
        query.addFilter(filter);
        T object = (T)query.getUniqueResult(parameterName, parameterValue);
        return object;
    }

    public <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        List<T> objects = query.execute();
        return objects;
    }

    public <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz, String orderBy)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        query.setOrderBy(orderBy);
        List<T> objects = query.execute();
        return objects;
    }

    /**
     * A list of results. Guaranteed to be not null.
     * @return non-null list (possibly empty)
     */
    public <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz,
                                                       String columnName, Object parameterValue)
    {
        Filter filter = new Filter(columnName, OP.EQ, new Param(P1));
        return getAllObjects(clazz, filter, P1, parameterValue);
    }

    /**
     * The return list is guaranteed not to be null. Will be empty or with values.
     * @return non-null list (possibly empty)
     */
    public <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz, Filter filter,
                                                       String parameterName, Object parameterValue)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        query.addFilter(filter);
        List<T> objects = query.execute(parameterName, parameterValue);
        return objects;
    }

    /**
     * The return list is guaranteed not to be null. Will be empty or with values.
     * @return non-null list (possibly empty)
     */
    public <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz, List<Filter> filters,
                                                       Map<String, Object> parameterValues)
    {
        return getAllObjects(clazz, filters, parameterValues, null, null, null);
    }
    
    public <T extends ObjectJDO> List<T> getAllObjects(Class<T> clazz, List<Filter> filters,
                                                       Map<String, Object> parameterValues,
                                                       String orderBy, Integer firstResult, Integer maxResults)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        for (Filter filter : filters)
        {
            query.addFilter(filter);
        }
        query.setOrderBy(orderBy);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        List<T> objects = query.execute(parameterValues);
        return objects;
    }

    public <T extends ObjectJDO, X> List<X> runQuery(Class<T> clazz, String[] selectColumns)
    {
        return runQuery(clazz, selectColumns, null);
    }

    public <T extends ObjectJDO> List<Object[]> runQuery(Class<T> clazz, String[] selectColumns,
                                                         String columnName, Object parameterValue)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        query.setSelectColumns(selectColumns);
        Filter filter = new Filter(columnName, OP.EQ, new Param(P1));
        String parameterName = P1;
        query.addFilter(filter);

        List<Object[]> objects = query.execute(parameterName, parameterValue);
        return objects;
    }
    
    public <T extends ObjectJDO> SimplifiedQuery createSimpleQuery(Class<T> clazz)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        return query;
    }

    public <T extends ObjectJDO, X> List<X> runQuery(Class<T> clazz, String[] selectColumns,
                                                     Filter filter)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SimplifiedQuery query = currentManager.newQuery(clazz);
        query.setSelectColumns(selectColumns);

        if (filter != null)
        {
            query.addFilter(filter);
        }

        List<X> objects = query.execute();
        return objects;
    }
    
    public <T> List<T> runQuery(String queryStr)
    {
        PersistenceManager currentManager = getPersistenceManager();
        GenericQuery query = currentManager.newQuery(queryStr);
        List<T> objects = query.execute();
        return objects;
    }
    
    public List<Object[]> runSQLQuery(String queryStr)
    {
        return runSQLQuery(queryStr, null);
    }
    
    public List<Object[]> runSQLQuery(String queryStr, String[] columnNames)
    {
        PersistenceManager currentManager = getPersistenceManager();
        SQLQuery query = currentManager.newSQLQuery(queryStr, columnNames);
        List<Object[]> objects = query.execute();
        return objects;
    }
    
    public ClassMetadata getMetadata(Class<?> clazz)
    {
        PersistenceManager currentManager = getPersistenceManager();
        return currentManager.getMetadata(clazz);
    }
    
}
