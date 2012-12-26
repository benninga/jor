package org.jor.server.services.db;

public class JdoObjectNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public JdoObjectNotFoundException(String message)
    {
        super(message);
    }
    
    public JdoObjectNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
