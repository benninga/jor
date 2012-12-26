package org.jor.server.services.db;

public class InvalidObjectException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public InvalidObjectException(String message)
    {
        super(message);
    }
    
    public InvalidObjectException(Throwable error)
    {
        super(error);
    }
}
