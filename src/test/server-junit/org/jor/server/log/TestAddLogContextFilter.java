package org.jor.server.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.junit.Test;

import org.jor.login.HttpRequestHandler;
import org.jor.login.HttpResponseHandler;
import org.jor.server.services.SessionManager;
import org.jor.server.services.db.MyExampleFilterChain;

public class TestAddLogContextFilter
{
    @Test public void testEmptyMethods()
    {
        AddLogContextFilter filter = new AddLogContextFilter();
        filter.destroy();
        filter.init(null);
    }
    @Test
    public void testDoFilter() throws Exception
    {
        AddLogContextFilter filter = new AddLogContextFilter();
        
        HttpServletRequest request =
            HttpRequestHandler.createHttpRequest(null, null, "index.html");
        HttpServletResponse response = HttpResponseHandler.createHttpResponse();
        MyExampleFilterChain chain = new MyExampleFilterChain();
        
        SessionManager.get().removeSession();
        SessionManager.get().createSession();
        filter.doFilter(request, response, chain);
        Assert.assertTrue("Filter chain should have been called", chain.isCalled());
    }
}
