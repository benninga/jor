package org.jor.access;

import org.jor.server.services.user.SecurityContext;

public interface AccessListener
{
    void verifyValidSession(SecurityContext session);
    boolean isSessionValid(SecurityContext session);
}
