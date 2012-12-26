package org.jor.jdo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import org.jor.model.ModelBean;
import org.jor.server.services.db.ObjectJDO;
import org.jor.server.services.db.ObjectKey;
import org.jor.shared.api.IModelObject;

@JsonIgnoreProperties("key")
@ModelBean(IModelObject.class)
public class ModelObject implements IModelObject, ObjectJDO
{
    private static final long serialVersionUID = 1L;
    private static final String DATE_TIME_FORMAT_PATTERN = "MM-dd-yyyy HH:mm:ss.SSS";
    private static final String DATE_FORMAT_PATTERN = "MM-dd-yyyy";
    protected static final String PROPERTIES = "properties";
    protected static final String PROPERTIES_STRING = "propertiesString";
    public static final String FIELD_TAG_ID = "tagId";

    private Long id;
    private String name;
    private String description;
    private Date lastUpdateAt;
    private Long createdBy;
    private Long tagId;
    
    public ModelObject()
    {
        id = null;
        name = null;
        description = null;
        lastUpdateAt = new Date();
        setCreatedBy(null);
    }
    
    public ModelObject(String name)
    {
        this();
        setName(name);
        
    }
    
    public ModelObject(Long id, String name, String description,
                       Date lastUpdateAt, Long createdBy)
    {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.lastUpdateAt = lastUpdateAt;
        this.createdBy = createdBy;
    }

    public ModelObject(ModelObject other)
    {
        this(other, true);
    }
    
    public ModelObject(ModelObject other, boolean copyId)
    {
        if (other == null) {
            throw new NullPointerException("Source object is null");
        }
        
        if (copyId) {
            setId(other.getId());
        }
        setName(other.getName());
        setDescription(other.getDescription());
        setLastUpdateAt(other.getLastUpdateAt());
        setCreatedBy(other.getCreatedBy());
        setTagId(other.getTagId());
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
    public String getName()
    {
        return name;
    }
    
    @Override
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String getDescription()
    {
        return description;
    }
    
    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public void setLastUpdateAt(Date lastUpdateAt)
    {
        if (lastUpdateAt == null) {
            lastUpdateAt = new Date();
        }
        this.lastUpdateAt = lastUpdateAt;
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
    
    @Override
    public Long getTagId()
    {
        return tagId;
    }
    
    @Override
    public void setTagId(Long tagId)
    {
        this.tagId = tagId;
    }
    
    @Override
    public ObjectKey getKey()
    {
        return new ObjectKey(getId());
    }
    
    public void setKey(@SuppressWarnings("unused") ObjectKey key)
    {
        // NOTE: remove after fixing jackson serialization.
    }
    
    protected static Long getLong(String id)
    {
        if (id == null || "".equals(id)) {
            return null;
        }
        return Long.parseLong(id);
    }
    
    protected static String toString(Object value)
    {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
    
    protected static Double getDouble(String value)
    {
        if (value == null || "".equals(value))
        {
            return null;
        }
        
        return Double.parseDouble(value);
    }
    
    protected static Integer getInt(String value)
    {
        if (value == null || "".equals(value))
        {
            return null;
        }
        
        return Integer.parseInt(value);
    }
    
    protected static Boolean getBoolean(String value)
    {
        if (value == null || "".equals(value))
        {
            return null;
        }
        
        return Boolean.parseBoolean(value);
    }
    
    protected static Date getDateTime(String value)
    {
        return getDate(value, DATE_TIME_FORMAT_PATTERN);
    }
    
    protected static Date getDate(String value)
    {
        return getDate(value, DATE_FORMAT_PATTERN);
    }
    
    private static Date getDate(String value, String formatPattern)
    {
        if (value == null || "".equals(value))
        {
            return null;
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
        try
        {
            Date date = formatter.parse(value);
            return date;
        }
        catch (ParseException e)
        {
            String msg = String.format("Badly formatted date '%s'. Use: %s", value, formatPattern);
            throw new RuntimeException(msg, e);
        }
    }
    
    protected static String toDateTimeString(Date date)
    {
        return toDateString(date, DATE_TIME_FORMAT_PATTERN);
    }
    
    protected static String toDateString(Date date)
    {
        return toDateString(date, DATE_FORMAT_PATTERN);
    }
    
    protected static String toDateString(Date date, String formatPattern)
    {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern);
        return formatter.format(date);
    }
    
    @Override
    public Map<String, Object> entityToMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, getId());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_DESCRIPTION, getDescription());
        map.put(FIELD_LAST_UPDATE_AT, toDateTimeString(getLastUpdateAt()));
        map.put(FIELD_CREATED_BY, getCreatedBy());
        return map;
    }

    @Override
    public void objectFromMap(MultivaluedMap<String, String> map)
    {
        setId(getLong(map.getFirst(FIELD_ID)));
        setName(map.getFirst(FIELD_NAME));
        setDescription(map.getFirst(FIELD_DESCRIPTION));
        setLastUpdateAt(getDateTime(map.getFirst(FIELD_LAST_UPDATE_AT)));
        setCreatedBy(getLong(map.getFirst(FIELD_CREATED_BY)));
    }

    @Override
    public String verifyObject()
    {
        return null;
    }
    
    @Override
    public int hashCode()
    {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other == this) {
            return true;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        ObjectJDO otherJDO = (ObjectJDO)other;
        if (getId() != NEW_COMPONENT_ID) {
            return getId().equals(otherJDO.getId());
        }
        return super.equals(other);
    }
}
