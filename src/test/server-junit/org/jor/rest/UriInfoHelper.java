package org.jor.rest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.ws.rs.core.UriInfo;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class UriInfoHelper implements InvocationHandler
{
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String methodName = method.getName();
        if ("getQueryParameters".equals(methodName))
        {
            return new MultivaluedMapImpl();
        }
        return null;
    }
    
    public static UriInfo createUriInfo()
    {
        InvocationHandler handler = new UriInfoHelper();
        UriInfo request = (UriInfo)
            Proxy.newProxyInstance(UriInfo.class.getClassLoader(), new Class<?>[] { UriInfo.class }, handler);
        return request;
    }
}
