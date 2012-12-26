package org.jor.server.services.db;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MyExampleFilterChain implements FilterChain
{
    private boolean called;
    private boolean throwError;
    
    public MyExampleFilterChain()
    {
        called = false;
        throwError = false;
    }
    
    public MyExampleFilterChain(boolean throwError)
    {
        this();
        this.throwError = throwError;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response)
    {
        called = true;
        if (throwError) {
            throw new RuntimeException("test exception");
        }
    }
    
    public boolean isCalled()
    {
        return called;
    }
    
    public void setCalled(boolean called)
    {
        this.called = called;
    }
}
