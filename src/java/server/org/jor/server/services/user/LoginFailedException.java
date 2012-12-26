package org.jor.server.services.user;

public class LoginFailedException extends Exception
{
    private static final long serialVersionUID = 1L;

    private final String username;
    
    public LoginFailedException(String message, String username)
    {
        super(message);
        this.username = username;
    }
    
    public String getUsername()
    {
        return username;
    }
}
