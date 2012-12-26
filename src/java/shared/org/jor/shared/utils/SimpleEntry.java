package org.jor.shared.utils;

public class SimpleEntry<K, V>
{
    private K key;
    private V value;
    
    public SimpleEntry(K key, V value)
    {
        this.key = key;
        this.value = value;
    }
    
    public K getKey()
    {
        return key;
    }
    
    public V getValue()
    {
        return value;
    }
}
