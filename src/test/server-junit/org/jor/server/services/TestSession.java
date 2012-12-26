package org.jor.server.services;

import org.jor.server.services.user.SecurityContext;

import junit.framework.Assert;

import org.junit.Test;

public class TestSession
{
    @Test
    public void testConstructor_1()
    {
        Session session = new Session();
        Assert.assertNull("Security context should be null", session.getSecurityContext());
    }
    @Test
    public void testConstructor_2()
    {
        SecurityContext sc = new SecurityContext("key", "name", "session id");
        Session session = new Session(sc);
        SecurityContext copy = session.getSecurityContext();
        Assert.assertNotNull("Security context should not be null", copy);
        Assert.assertEquals("Same security context", sc, copy);
    }
}
