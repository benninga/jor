package org.jor.server.services.db;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.jor.jdo.BaseJdoTestClass;
import org.jor.login.HttpRequestHandler;

public class TestDataLayerThreadFilter extends BaseJdoTestClass
{
    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        for (String namedSource : DataService.getNamedSources())
        {
        	DataService.getDataService(namedSource).closeSession();
        }
    }
    
    @Test public void testEmptyMethods()
    {
        DataLayerThreadFilter filter = new DataLayerThreadFilter();
        filter.destroy();
        filter.init(null);
    }
    
    @Test public void testDoFilter() throws Exception
    {
        DataLayerThreadFilter filter = new DataLayerThreadFilter();
        MyExampleFilterChain chain = new MyExampleFilterChain();
        HttpServletRequest request = HttpRequestHandler.createHttpRequest();
        
        Assert.assertFalse("Verify the filter chain was not called yet", chain.isCalled());
        filter.doFilter(request, null, chain);
        Assert.assertTrue("Verify the filter chain was called", chain.isCalled());
    }
    
    @Test public void testExceptionHandle() throws Exception
    {
        DataLayerThreadFilter filter = new DataLayerThreadFilter();
        MyExampleFilterChain chain = new MyExampleFilterChain(true);
        
        try
        {
            filter.doFilter(null, null, chain);
            Assert.fail("A ServletException should have been thrown");
        }
        catch (RuntimeException e)
        {
            // This is expected.
        }
    }
    
}
