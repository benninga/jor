package org.jor.jdo;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import org.jor.server.services.ServicesManager;
import org.jor.server.services.SessionManager;
import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectJDO;
import org.jor.server.services.db.PersistenceManager;
import org.jor.server.services.user.SecurityContext;
import org.jor.utils.CopyConstructorHelper;

abstract public class BaseJdoTestClass
{
    private static final Collection<Class<?>> JDO_CLASSES;

    @Before
    public void setUp() throws Exception
    {
        ServicesManager.createDefaultContent = false;
        SessionManager.get().removeSession();
        SessionManager.get().createSession();

        // Setup a fake security context.
        SecurityContext sc = new SecurityContext("1", "user", "hello");
        SessionManager.get().clear();
        SessionManager.get().createSession(sc);
        
        // Setup the data service properly.
        String dbName = ServicesManager.DEFAULT_DB;
        DataService.setDefaultDatabaseName(dbName);
        DataService.getDataService(dbName);
        startDataServices();
    }

    @After
    public void tearDown() throws Exception
    {
        SessionManager.get().removeSession();
        stopDataServices();
    }

    private void startDataServices()
    {
        for (String namedSource : DataService.getNamedSources())
        {
            PersistenceManager.setSessionFactory(namedSource, setupTestDatabase().buildSessionFactory());
            DataService.getDataService(namedSource).openSession();
        }
    }

    private void stopDataServices()
    {
        PersistenceManager.setSessionFactory(null);
        for (String namedSource : DataService.getNamedSources())
        {
                DataService.getDataService(namedSource).closeSession();
        }
    }

    protected static Configuration setupTestDatabase()
    {
        Configuration config = new Configuration();
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
               .setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
               .setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:jor")
               .setProperty("hibernate.connection.username", "sa")
               .setProperty("hibernate.connection.password", "")
               .setProperty("hibernate.connection.pool_size", "1")
               .setProperty("hibernate.connection.autocommit", "true")
               .setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider")
               .setProperty("hibernate.hbm2ddl.auto", "create-drop")
               .setProperty("hibernate.show_sql", "true");

        for (Class<?> clazz : JDO_CLASSES)
        {
            config.addClass(clazz);
        }
        return config;
    }

    public static void testAllFieldsSavedProperly(Class<? extends ObjectJDO> clazz)
        throws Exception
    {
        ObjectJDO value = (ObjectJDO)CopyConstructorHelper.constructRandom(clazz);
        testAllFieldsSavedProperly(value);
    }

    public static void testAllFieldsSavedProperly(ObjectJDO value)
        throws Exception
    {
        DataService service = DataService.getDataService();
        ObjectJDO copied = (ObjectJDO)CopyConstructorHelper.copyConstruct(value);
        ObjectJDO updated = (ObjectJDO)CopyConstructorHelper.copyConstruct(service.save(copied));
        String result = null;
        result = CopyConstructorHelper.compareObjects(copied, updated, true);
        Assert.assertNull("Object was not the same after save: \n" + result, result);

        // Flush the session so we don't get a cached object.
        service.closeSession();
        service.openSession();

        ObjectJDO loaded = service.getObject(value.getClass(), updated.getKey());
        result = CopyConstructorHelper.compareObjects(copied, loaded, true);
        Assert.assertNull("Object was not the same after save: \n" + result, result);
    }

    static
    {
        JDO_CLASSES = new HashSet<>();
        JDO_CLASSES.add(SampleClassJDO.class);
    }
}
