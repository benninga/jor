package org.jor.utils;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestCacheMap
{
    private static final String MAP_SIZE = "Map size";

    @Test
    public void testMapSizeMaintained()
    {
        Map<Integer, String> map = new CacheMap<>(3);
        map.put(1, "1");
        Assert.assertEquals(MAP_SIZE, 1, map.size());
        map.put(2, "2");
        Assert.assertEquals(MAP_SIZE, 2, map.size());
        map.put(3, "3");
        Assert.assertEquals(MAP_SIZE, 3, map.size());
        map.put(4, "4");
        Assert.assertEquals(MAP_SIZE, 3, map.size());
        map.put(5, "5");
        Assert.assertEquals(MAP_SIZE, 3, map.size());
    }
    
    @Test
    public void testMapUpdatedOnRemove()
    {

        Map<Integer, String> map = new CacheMap<>(3);
        map.put(1, "1");
        map.put(2, "2");
        map.put(100, "Bad Key");
        map.remove(100);
        map.put(3, "3");
        
        Assert.assertTrue(String.format("Map contains key %s", 1), map.containsKey(1));
        Assert.assertTrue(String.format("Map contains key %s", 2), map.containsKey(2));
        Assert.assertTrue(String.format("Map contains key %s", 3), map.containsKey(3));
        Assert.assertEquals(MAP_SIZE, 3, map.size());
    }
}
