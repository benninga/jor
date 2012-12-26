package org.jor.server.services.db;

public class ConstraintViolationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public ConstraintViolationException(String message)
    {
        super(message);
    }
    
    public ConstraintViolationException(Throwable error)
    {
        super(error);
    }
}
