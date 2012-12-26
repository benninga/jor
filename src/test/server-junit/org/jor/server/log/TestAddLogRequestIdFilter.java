package org.jor.server.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.junit.Test;

import org.jor.login.HttpRequestHandler;
import org.jor.login.HttpResponseHandler;
import org.jor.server.services.db.MyExampleFilterChain;

// Extends BaseJdoTestClass because AddLogRequestIdFilter requires DB access
public class TestAddLogRequestIdFilter
{
    @Test public void testEmptyMethods()
    {
        AddLogRequestIdFilter filter = new AddLogRequestIdFilter();
        filter.destroy();
        filter.init(null);
    }
    @Test
    public void testDoFilter() throws Exception
    {
        AddLogRequestIdFilter filter = new AddLogRequestIdFilter();
        
        HttpServletRequest request = HttpRequestHandler.createHttpRequest(null, null, "/index.html");
        HttpServletResponse response = HttpResponseHandler.createHttpResponse();
        MyExampleFilterChain chain = new MyExampleFilterChain();
        
        filter.doFilter(request, response, chain);
        Assert.assertTrue("Filter chain should have been called", chain.isCalled());
    }
}
