package org.jor.server.services.user;

import java.io.Serializable;

public class SecurityContext implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public static final String ANONYMOUS_USER_KEY = null;
    public static final String ANONYMOUS_USER_NAME = "Anonymous";
    public static final String INVALID_SESSION = "";
    
    public static final String SYSTEM_USER_KEY = "-2";
    public static final String SYSTEM_USER_NAME = "System";
    
    private String userKey;
    private String username;
    private String runAsUserKey;
    private String runAsUsername;
    private String sessionId;
    
    private int hashCode;
    
    // GWT needs an empty constructor for serialization.
    public SecurityContext()
    {
        userKey = ANONYMOUS_USER_KEY;
        username = ANONYMOUS_USER_NAME;
        runAsUserKey = null;
        runAsUsername = null;
        sessionId = INVALID_SESSION;
        updateHashCode();
    }
    
    public SecurityContext(String _userKey, String _userName, String _sessionId)
    {
        userKey = _userKey;
        username = _userName;
        sessionId = _sessionId;
        runAsUserKey = null;
        runAsUsername = null;
        updateHashCode();
    }
    
    public SecurityContext(String _userKey, String _userName, String _sessionId, String _runAsUserKey, String _runAsUsername)
    {
        userKey = _userKey;
        username = _userName;
        sessionId = _sessionId;
        runAsUserKey = _runAsUserKey;
        runAsUsername = _runAsUsername;
        updateHashCode();
    }
    
    public SecurityContext(SecurityContext other)
    {
        setUserKey(other.getUserKey());
        setUsername(other.getUsername());
        setRunAsUserKey(other.getRunAsUserKey());
        setRunAsUsername(other.getRunAsUsername());
        setSessionId(other.getSessionId());
        updateHashCode();
    }
    
    public String getEffectiveUserKey()
    {
        String key = (getRunAsUserKey() != null) ? getRunAsUserKey() : getUserKey();
        return key;
    }
    
    public String getUserKey()
    {
        return userKey;
    }
    
    private void setUserKey(String userKey)
    {
        this.userKey = userKey;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    private void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getRunAsUserKey()
    {
        return runAsUserKey;
    }
    
    private void setRunAsUserKey(String runAsUserKey)
    {
        this.runAsUserKey = runAsUserKey;
    }
    
    public String getRunAsUsername()
    {
        return runAsUsername;
    }
    
    private void setRunAsUsername(String runAsUsername)
    {
        this.runAsUsername = runAsUsername;
    }
    
    public String getSessionId()
    {
        return sessionId;
    }
    
    private void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }
    
    private void updateHashCode()
    {
        hashCode = ("" + userKey + username + sessionId).hashCode();
    }
    
    @Override public int hashCode()
    {
        return hashCode;
    }
    
    @Override public boolean equals(Object other)
    {
        if(other instanceof SecurityContext == false) {
            return false;
        }
        SecurityContext otherSession = (SecurityContext)other;
        
        if(match(getSessionId(), otherSession.getSessionId()) == false)
        {
            return false;
        }
        if(match(getUserKey(), otherSession.getUserKey()) == false)
        {
            return false;
        }
        if(match(getUsername(), otherSession.getUsername()) == false)
        {
            return false;
        }
        if(match(getRunAsUserKey(), otherSession.getRunAsUserKey()) == false)
        {
            return false;
        }
        if(match(getRunAsUsername(), otherSession.getRunAsUsername()) == false)
        {
            return false;
        }
        return true;
    }
    
    private boolean match(String a, String b)
    {
        if (a == null) {
            return b == null;
        }
        else
        {
            return a.equals(b);
        }
    }
}
