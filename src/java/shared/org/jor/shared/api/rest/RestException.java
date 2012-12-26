package org.jor.shared.api.rest;

public class RestException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    private final IRestError restError;
    
    public RestException(IRestError restError)
    {
        super(restError.getMessage());
        this.restError = restError;
    }
    
    public IRestError getRestError()
    {
        return restError;
    }
    
    @Override
    public String toString()
    {
        return "http code = " + restError.getHttpErrorCode()
               + ", internal code = " + restError.getInternalErrorCode()
               + ", message = " + restError.getMessage();
    }
}
