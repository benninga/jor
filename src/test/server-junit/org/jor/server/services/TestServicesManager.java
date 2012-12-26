package org.jor.server.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jor.jdo.BaseJdoTestClass;
import org.jor.server.services.db.DataService;
import org.jor.shared.log.LoggerFactory;
import org.jor.shared.log.LoggerProvider;

public class TestServicesManager extends BaseJdoTestClass
{
    private LoggerProvider lastProvider;
    
    @Before @Override
    public void setUp() throws Exception
    {
        super.setUp();
        lastProvider = LoggerFactory.getLoggerProvider();
        ServicesManager.defaultInit();
        ServicesManager.createDefaultContent = false;
    }
    
    @After @Override 
    public void tearDown() throws Exception
    {
        super.tearDown();
        LoggerFactory.setLoggerProvider(lastProvider);
        ServicesManager.defaultInit();
        ServicesManager.createDefaultContent = false;
    }
    
    @Test public void testServicesManager()
    {
        ServicesManager manager = new ServicesManager();
        manager.contextDestroyed(null);
        for (String namedSource : DataService.getNamedSources())
        {
        	DataService.getDataService(namedSource).closeSession();
        }
        
        LoggerFactory.setLoggerProvider(null);
        Assert.assertNull("Logger Provider should be null", LoggerFactory.getLoggerProvider());
        
        ServletContext context = (ServletContext)
            Proxy.newProxyInstance(ServletContext.class.getClassLoader(),
                                   new Class<?>[] { ServletContext.class }, new ServletContextHandler());
        ServletContextEvent event = new ServletContextEvent(context);
        manager.contextInitialized(event);
        Assert.assertNotNull("Logger Provider should not be null", LoggerFactory.getLoggerProvider());
    }
    
    protected static class ServletContextHandler implements InvocationHandler
    {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String methodName = method.getName();
            return methodName;
        }
    }
    
    @Test public void testGetterSetter()
    {
        String path = "Path-" + Math.random();
        String url = "URL-" + Math.random();
        String name = "Name-" + Math.random();
        ServicesManager.setApplicationName(name);
        ServicesManager.setApplicationRealPath(path);
        ServicesManager.setContextPath(url);
        
        Assert.assertEquals("Path was not the same", path, ServicesManager.getApplicationRealPath());
        Assert.assertEquals("URL was not the same", url, ServicesManager.getContextPath());
        Assert.assertEquals("Name was not the same", name, ServicesManager.getApplicationName());
    }
}
