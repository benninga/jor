package org.jor.shared.api;

import java.util.Date;

public interface IModelObject extends IApiInterface
{
    Long NEW_COMPONENT_ID = null;
    final String FIELD_ID = "id";
    final String FIELD_NAME = "name";
    final String FIELD_DESCRIPTION = "description";
    final String FIELD_LAST_UPDATE_AT = "lastUpdateAt";
    final String FIELD_CREATED_BY = "createdBy";
    
    Long getId();
    void setId(Long id);
    
    String getName();
    void setName(String name);
    
    String getDescription();
    void setDescription(String description);
    
    Date getLastUpdateAt();
    void setLastUpdateAt(Date lastUpdateAt);

    void setCreatedBy(Long createdBy);
    Long getCreatedBy();

    /**
     * This is a field used to identify different, unsaved objects.
     * Mostly used for GUI to deal with unsaved objects.
     * 
     * The field is transient (not saved to DB) 
     */
    Long getTagId();
    void setTagId(Long tagId);
}
