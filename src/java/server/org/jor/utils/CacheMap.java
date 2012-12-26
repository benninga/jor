package org.jor.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheMap<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = 1L;
    
    private int maxSize = 100;
    
    public CacheMap()
    {
        this(100);
    }
    
    public CacheMap(int maxSize)
    {
        super(maxSize, 0.75F, true);
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> oldestEntry)
    {
        return size() > maxSize;
    }
}
