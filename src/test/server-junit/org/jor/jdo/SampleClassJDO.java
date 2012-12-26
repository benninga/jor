package org.jor.jdo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.jor.server.services.db.ObjectJDO;
import org.jor.server.services.db.ObjectKey;

/**
 * This class is used to test JDO interactions (queries, load, save, etc)
 * By having a test class, we can ensure it does not change
 * (unlike application classes that may change their schema)
 * and make unit tests more stable
 * 
 */
public class SampleClassJDO implements ObjectJDO, Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Date lastUpdateAt;
    private String errorResult;
    private Long createdBy;
    
    public SampleClassJDO()
    {
        lastUpdateAt = new Date();
    }
    
    @Override
    public ObjectKey getKey()
    {
        return getClassKey();
    }
    
    public ObjectKey getClassKey()
    {
        return new ObjectKey(id);
    }
    
    public void setClassKey(ObjectKey key)
    {
        this.id = key.getId();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public void setLastUpdateAt(Date updateDate)
    {
        this.lastUpdateAt = updateDate;
    }
    
    @Override
    public Date getLastUpdateAt()
    {
        return lastUpdateAt;
    }

    @Override
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }

    @Override
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setErrorString(String errorMessage)
    {
        this.errorResult = errorMessage;
    }
    
    @Override public String verifyObject()
    {
        return errorResult;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public Map<String, Object> entityToMap()
    {
        return null;
    }

    @Override
    public void objectFromMap(MultivaluedMap<String, String> map)
    {
        // Do nothing for now.
    }
}
