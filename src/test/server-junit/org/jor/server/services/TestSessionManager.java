package org.jor.server.services;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSessionManager
{
    private static final String SESSION_NULL = "Session should be null";
    
    
    @Before
    @After
    public void setUp()
    {
        SessionManager.get().clear();
    }
    
    @Test
    public void testCreateSession()
    {
        SessionManager mgr = SessionManager.get();
        
        // Verify null security context is disallowed
        try
        {
            mgr.createSession(null);
            Assert.fail("An NPE should have been thrown");
        }
        catch (NullPointerException e)
        {
            // This is expected.
        }
        
        // Verify we can create a simple session.
        mgr.createSession();
        
        // Trying to create a session whe one exists should fail.
        try
        {
            mgr.createSession();
            Assert.fail("An Illegal state exception should have been thrown");
        }
        catch (IllegalStateException e)
        {
            // This is expected.
        }
    }
    
    @Test
    public void testGetRemoveSession()
    {
        SessionManager mgr = SessionManager.get();
        Session session = mgr.getSession();
        Assert.assertNull(SESSION_NULL, session);
        
        mgr.createSession();
        session = mgr.getSession();
        Assert.assertNotNull("Session should not be null", session);
        
        mgr.removeSession();
        session = mgr.getSession();
        Assert.assertNull(SESSION_NULL, session);
    }
    
    @Test
    public void testRemoveSession()
    {
        SessionManager mgr = SessionManager.get();
        
        // Make sure we can call remove session even if no session exists
        Session session = mgr.removeSession();
        Assert.assertNull(SESSION_NULL, session);
        
        mgr.createSession();
        session = mgr.removeSession();
        Assert.assertNotNull("Session should not be null", session);
        session = mgr.getSession();
        Assert.assertNull(SESSION_NULL, session);
        
        // Verify we can call remove session again with no errors
        mgr.removeSession();
    }
}
