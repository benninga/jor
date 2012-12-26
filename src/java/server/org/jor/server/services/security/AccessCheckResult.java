package org.jor.server.services.security;

public class AccessCheckResult
{
    private boolean deny = false;
    private boolean allow = false;
    private String denyReason;
    
    public boolean getHasAccess()
    {
        if (deny) {
            return false;
        }
        else if (allow) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public AccessCheckResult deny()
    {
        this.deny = true;
        return this;
    }
    
    public AccessCheckResult allow()
    {
        this.allow = true;
        return this;
    }
    
    public String getDenyReason()
    {
        return denyReason;
    }
    
    public AccessCheckResult setDenyReason(String denyReason)
    {
        this.denyReason = denyReason;
        return this;
    }
}
