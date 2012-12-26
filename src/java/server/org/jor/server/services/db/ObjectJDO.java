package org.jor.server.services.db;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public interface ObjectJDO extends Serializable
{
    Long getId();
    void setId(Long id);
    ObjectKey getKey();
    Map<String, Object> entityToMap();
    void objectFromMap(MultivaluedMap<String, String> map);
    
    void setLastUpdateAt(Date date);
    Date getLastUpdateAt();
    
    Long getCreatedBy();
    void setCreatedBy(Long id);
    
    /**
     * Verifies that a JDO object is corret and valid. This will be used before
     * saving a JDO object to the database.
     * 
     * @return null if the object is correct and an error string describing the issue if the
     * object is not correct or valid.
     */
    String verifyObject(); 
}
