package org.jor.server.services.db;

import org.jor.server.services.db.TestTransaction.MyTransaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPersistenceManager
{
    @Before
    public void setUp()
    {
        PersistenceManager.setSessionFactory(null);
    }
    
    @After
    public void tearDown()
    {
        PersistenceManager.setSessionFactory(null);
    }
    
    @Test
    public void testBuildSessionFactory()
    {
        SessionFactory factory = PersistenceManager.buildSessionFactory();
        Assert.assertNotNull("Not null session factory_1", factory);
    }
    
    @Test
    public void testGetSessionFactory()
    {
        PersistenceManager.setSessionFactory(null);
        SessionFactory factory = PersistenceManager.getSessionFactory();
        Assert.assertNotNull("Not null session factory_2", factory);
        
        // Second time we should get a cached version that was already built
        SessionFactory factory2 = PersistenceManager.getSessionFactory();
        Assert.assertNotNull("Not null session factory_3", factory2);
        Assert.assertSame("Same session factory", factory, factory2);
        
        InvocationHandler handler = new InvocationHandler()
        {
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                return null;
            }
        };
        SessionFactory myFactory = (SessionFactory)
            Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { SessionFactory.class }, handler);
        PersistenceManager.setSessionFactory(myFactory);
        factory = PersistenceManager.getSessionFactory();
        Assert.assertSame("Same session factory", myFactory, factory);
        
    }
    
    @Test public void testGetGenericQuery()
    {
        PersistenceManager mgr = new PersistenceManager(DataService.getDefaultDatabaseName());
        GenericQuery query = mgr.newQuery("My String query");
        Assert.assertNotNull("Got generic query object", query);
    }
    
    @Test public void testGetSQLQuery()
    {
        PersistenceManager mgr = new PersistenceManager(DataService.getDefaultDatabaseName());
        SQLQuery query = mgr.newSQLQuery("My String query");
        Assert.assertNotNull("Got generic query object", query);
    }
    
    protected static Session createSession(boolean failOnFlush)
    {
        Session session = (Session)
            Proxy.newProxyInstance(TestTransaction.class.getClassLoader(),
                                   new Class<?>[] { Session.class }, new MySession(failOnFlush));
        return session;
    }
    
    static class MySession implements InvocationHandler
    {
        private boolean failOnFlush;
        
        protected MySession(boolean failOnFlush)
        {
            this.failOnFlush = failOnFlush;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String methodName = method.getName();
            if ("getTransaction".equals(methodName))
            {
                return Proxy.newProxyInstance(TestTransaction.class.getClassLoader(),
                                              new Class<?>[] { org.hibernate.Transaction.class}, new MyTransaction());
            }
            else if ("flush".equals(methodName))
            {
                if (failOnFlush) {
                    throw new TransientObjectException("Fail on purpose");
                } else {
                    return null;
                }
            }
            throw new RuntimeException("Unimplemented method: " + methodName);
        }
    }
}
