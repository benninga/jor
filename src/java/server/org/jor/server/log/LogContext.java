package org.jor.server.log;

import org.slf4j.MDC;

public class LogContext
{
    public static final String IP_ADDRESS = "ip";
    public static final String USER_NAME = "username";
    public static final String REQUEST_ID = "request";
    public static final String URL = "url";
    public static final String HTTP_METHOD = "http_method";
    public static final String HTTP_USER_AGENT = "User-Agent";
    public static final String HTTP_REFERRER = "Referer";
    public static final String HTTP_ORIGIN = "Origin";
    
    private static final LogContext instance = new LogContext();
    
    public static LogContext get()
    {
        return instance;
    }
    
    public void setRemoteIP(String value)
    {
        put(IP_ADDRESS, value);
    }
    
    public String getRemoteIP()
    {
        return get(IP_ADDRESS);
    }
    
    public void removeRemoteIP()
    {
        remove(IP_ADDRESS);
    }
    
    public void setUserName(String value)
    {
        put(USER_NAME, value);
    }
    
    public String getUserName()
    {
        return get(USER_NAME);
    }
    
    public void removeUserName()
    {
        remove(USER_NAME);
    }
    
    public void setRequestId(String value)
    {
        put(REQUEST_ID, value);
    }
    
    public String getRequestId()
    {
        return get(REQUEST_ID);
    }
    
    public void removeRequestId()
    {
        remove(REQUEST_ID);
    }
    
    public void setHttpMethod(String url)
    {
        put(HTTP_METHOD, url);
    }
    
    public String getHttpMethod()
    {
        return get(HTTP_METHOD);
    }
    
    public void removeHttpMethod()
    {
        remove(HTTP_METHOD);
    }
    
    public void setUrl(String url)
    {
        put(URL, url);
    }
    
    public String getUrl()
    {
        return get(URL);
    }
    
    public void removeUrl()
    {
        remove(URL);
    }
    
    public void setUserAgent(String userAgent)
    {
        put(HTTP_USER_AGENT, userAgent);
    }
    
    public String getUserAgent()
    {
        return get(HTTP_USER_AGENT);
    }
    
    public void removeUserAgent()
    {
        remove(HTTP_USER_AGENT);
    }
    
    public void setReferrer(String referrer)
    {
        put(HTTP_REFERRER, referrer);
    }
    
    public String getReferrer()
    {
        return get(HTTP_REFERRER);
    }
    
    public void removeReferrer()
    {
        remove(HTTP_REFERRER);
    }
    
    public void setOrigin(String origin)
    {
        put(HTTP_ORIGIN, origin);
    }
    
    public String getOrigin()
    {
        return get(HTTP_ORIGIN);
    }
    
    public void removeOrigin()
    {
        remove(HTTP_ORIGIN);
    }
    
    private void put(String name, String value)
    {
        if (value == null) {
            value = "";
        }
        MDC.put(name, value);
    }
    
    private void remove(String name)
    {
        MDC.remove(name);
    }
    
    private String get(String name)
    {
        return MDC.get(name);
    }
}
