package org.jor.server.services;

import org.jor.server.services.user.SecurityContext;
import org.jor.utils.CacheMap;

public class Session
{
    private SecurityContext context;
    private CacheMap<Object, Object> sessionCache;
    
    public static enum CacheType
    {
        RATED, REFERENCE, HERS_MODIFIED, NO_CACHE;
    }

    public Session()
    {
        this.context = null;
        this.sessionCache = new CacheMap<>(100);
    }
    
    public Session(SecurityContext context)
    {
        this();
        this.context = context;
    }
    
    public SecurityContext getSecurityContext()
    {
        return context;
    }
    
    public void setInCache(Object key, Object value)
    {
        sessionCache.put(key, value);
    }
    
    public boolean isInCache(Object key)
    {
        return sessionCache.containsKey(key);
    }
    
    public Object getFromCache(Object key)
    {
        return sessionCache.get(key);
    }
}
