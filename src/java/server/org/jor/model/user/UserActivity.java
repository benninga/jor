package org.jor.model.user;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.jor.jdo.ModelObject;
import org.jor.server.services.db.ObjectJDO;


@XmlRootElement
public class UserActivity extends ModelObject implements ObjectJDO
{
    private static final long serialVersionUID = 1L;
    
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_USERKEY = "userkey";
    public static final String FIELD_HTTP_METHOD = "httpMethod";
    public static final String FIELD_REQUEST = "request";
    public static final String FIELD_INSTANCE = "instance";
    public static final String FIELD_DURATION = "duration";
    
    private Date timestamp = new Date();
    private String username = "";
    private Long userkey = 0L;
    private String httpMethod = ""; // http method: GET, POST, PUT, CREATE
    private String request = "";
    private long duration = 0;
    private String instance = "";
    
    public Long getUserkey()
    {
        return userkey;
    }
    
    public void setUserkey(Long userkey)
    {
        this.userkey = userkey;
    }
    
    public String getInstance()
    {
        return instance;
    }
    
    public void setInstance(String instance)
    {
        this.instance = instance;
    }
    
    public Date getTimestamp() 
    {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getHttpMethod()
    {
        return httpMethod;
    }
    
    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }
    
    public String getRequest()
    {
        return request;
    }
    
    public void setRequest(String request)
    {
        this.request = request;
    }
    
    public long getDuration()
    {
        return duration;
    }
    
    public void setDuration(long duration)
    {
        this.duration = duration;
    }
    
}
