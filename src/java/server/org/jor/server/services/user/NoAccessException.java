package org.jor.server.services.user;

import org.jor.server.services.security.AccessCheckResult;

public class NoAccessException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    // GWT requires empty constructor
    public NoAccessException()
    {
        // GWT requires empty constructor
    }
    
    public NoAccessException(String message)
    {
        super(message);
    }
    
    public NoAccessException(AccessCheckResult result)
    {
        super(result.getDenyReason());
    }
}
