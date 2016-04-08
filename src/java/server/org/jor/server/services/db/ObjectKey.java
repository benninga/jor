package org.jor.server.services.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"id", "version"})
public class ObjectKey
{
    private static final String COLON = ":";
    private Long id;
    private Long version;
    
    public ObjectKey()
    {
        id = null;
        version = null;
    }
    
    public ObjectKey(Long id)
    {
        this();
        this.id = id;
    }
    
    public ObjectKey(Long id, Long version)
    {
        this();
        this.id = id;
        this.version = version;
    }
    
    public ObjectKey(String stringKey)
    {
        this();
        
        if(stringKey == null || "".equals(stringKey)) {
            return;
        }
        
        String[] elements = stringKey.split(COLON);
        this.id = Long.parseLong(elements[0]);
        if(elements.length > 1)
        {
            version = Long.parseLong(elements[1]);
        }
    }
    
    public Long getId()
    {
        return id;
    }

    public Long getVersion()
    {
        return version;
    }
    
    public static String convertToString(Object key)
    {
        if (key instanceof ObjectKey)
        {
            return convert((ObjectKey) key);
        }
        else if (key instanceof String)
        {
            return (String) key;
        }
        else
        {
            throw new IllegalArgumentException("Cannot convert key of type: " + key.getClass());
        }
    }
    
    public String convert()
    {
        return ObjectKey.convert(this);
    }

    public static ObjectKey convert(String stringKey)
    {
        if(stringKey == null) {
            return new ObjectKey();
        }
        return new ObjectKey(stringKey);
    }

    public static String convert(ObjectKey key)
    {
        if(key == null) {
            return null;
        }
        
        Long id = key.getId();
        Long version = key.getVersion();
        
        if (id == null) {
            return null;
        }
        
        String stringKey = id.toString();
        if (version != null)
        {
            stringKey += COLON + version.toString();
        }
        return stringKey;
    }
    
    @Override public int hashCode()
    {
        int idHash = (id == null) ? 1 : id.hashCode();
        int versionHash = (version == null) ? 1 : version.hashCode();

        int hash = idHash * versionHash;
        return hash;
    }
    
    @Override public boolean equals(Object other)
    {
        if (other == null) {
            return false;
        }
        
        if (other instanceof ObjectKey == false)
        {
            return false;
        }
        
        ObjectKey otherKey = (ObjectKey)other;
        Long otherId = otherKey.getId();
        if(id == null)
        {
            if(otherId != null) {
                return false;
            }
            // else both id == null && otherId == null
        }
        else if (id.equals(otherId) == false) 
        {
            return false;
        }
        
        Long otherVersion = ((ObjectKey)other).getVersion();
        if(version == null)
        {
            if(otherVersion != null) {
                return false;
            }
            // else both version == null && otherVersion == null
        }
        else if (version.equals(otherVersion) == false) 
        {
            return false;
        }
        
        return true;
    }
    
    @Override public String toString()
    {
        return "ObjectKey:" + id + COLON + version;
    }
    
}

