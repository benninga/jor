package org.jor.login;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpRequestHandler implements InvocationHandler
{
    private Map<String, String> parameters;
    private HttpSession session;
    private Cookie[] cookies;
    private String servletPath;
    private String pathInfo;
    
    private static final String GET_PARAMETER = "getParameter";
    private static final String GET_SESSION = "getSession";
    private static final String GET_COOKIES = "getCookies";
    private static final String GET_SERVLET_PATH = "getServletPath";
    private static final String GET_PATH_INFO = "getPathInfo";
    private static final String GET_ATTRIBUTE = "getAttribute";
    private static final String SET_ATTRIBUTE = "setAttribute";
    
    public HttpRequestHandler(Map<String, String> parameters, Cookie[] cookies,
                              String servletPath, String pathInfo)
    {
        this.parameters = parameters;
        this.cookies = cookies;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
        
        InvocationHandler handler = new SessionHandler();
        session = (HttpSession)
            Proxy.newProxyInstance(HttpSession.class.getClassLoader(),
                                   new Class<?>[] { HttpSession.class }, handler);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String methodName = method.getName();
        if(GET_PARAMETER.equals(methodName))
        {
            String name = (String)args[0];
            return parameters.get(name);
        }
        else if (GET_SESSION.equals(methodName))
        {
            return session;
        }
        else if (GET_COOKIES.equals(methodName))
        {
            return cookies;
        }
        else if (GET_SERVLET_PATH.equals(methodName))
        {
            return servletPath;
        }
        else if (GET_PATH_INFO.equals(methodName))
        {
            return pathInfo;
        }
        return null;
    }

    protected static class SessionHandler implements InvocationHandler
    {
        private Map<String, Object> attributes = new HashMap<>();
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String methodName = method.getName();
            if(GET_ATTRIBUTE.equals(methodName))
            {
                String name = (String)args[0];
                return attributes.get(name);
            }
            else if (SET_ATTRIBUTE.equals(methodName))
            {
                String name = (String)args[0];
                Object value = args[1];
                return attributes.put(name, value);
            }
            return null;
        }
    }
    
    public static HttpServletRequest createHttpRequest()
    {
        return createHttpRequest(null, null);
    }
    
    public static HttpServletRequest createHttpRequest(Map<String, String> parameters,
                                                       Cookie[] cookies)
    {
        return createHttpRequest(parameters, cookies, "", "");
    }
    
    public static HttpServletRequest createHttpRequest(Map<String, String> parameters,
                                                       Cookie[] cookies,
                                                       String pathInfo)
    {
        return createHttpRequest(parameters, cookies, "", pathInfo);
    }
    
    public static HttpServletRequest createHttpRequest(Map<String, String> parameters,
                                                       Cookie[] cookies,
                                                       String servletPath, String pathInfo)
    {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        if (cookies == null) {
            cookies = new Cookie[0];
        }
        
        InvocationHandler handler =
            new HttpRequestHandler(parameters, cookies, servletPath, pathInfo);
        HttpServletRequest request = (HttpServletRequest)
            Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                                   new Class<?>[] { HttpServletRequest.class }, handler);

        return request;
    }
}
