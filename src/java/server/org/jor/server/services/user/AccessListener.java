package org.jor.server.services.user;

public interface AccessListener
{
    void verifyValidSession(SecurityContext session) throws NoAccessException;
    boolean isSessionValid(SecurityContext session);
}
