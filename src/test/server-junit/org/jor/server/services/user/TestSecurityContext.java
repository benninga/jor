package org.jor.server.services.user;

import junit.framework.Assert;

import org.junit.Test;

public class TestSecurityContext
{
    private static final String USER_ID_1 = "1";
    private static final String USER_ID_2 = "2";
    
    private static final String SESSION_ID = "session";
    private static final String USER_NAME = "user";
    private static final String USER_KEY = "userKey";
    
    @Test public void testEmptyConstructor()
    {
        // GWT needs an empty constructor. Verify we have one
        new SecurityContext();
    }
    
    @Test public void testSecurityContextEquality()
    {
        SecurityContext s1, s2;
        
        s1 = new SecurityContext(USER_KEY, USER_NAME, SESSION_ID);
        s2 = new SecurityContext(USER_KEY, USER_NAME, SESSION_ID);
        Assert.assertTrue(s1.equals(s2));
        Assert.assertTrue(s2.equals(s1));
        

        s1 = new SecurityContext(USER_ID_1, USER_NAME, SESSION_ID);
        s2 = new SecurityContext(USER_ID_2, USER_NAME, SESSION_ID);
        verifyNotEqual(s1, s2);

        s1 = new SecurityContext(USER_KEY, USER_ID_1, SESSION_ID);
        s2 = new SecurityContext(USER_KEY, USER_ID_2, SESSION_ID);
        verifyNotEqual(s1, s2);

        s1 = new SecurityContext(USER_KEY, USER_NAME, USER_ID_1);
        s2 = new SecurityContext(USER_KEY, USER_NAME, USER_ID_2);
        verifyNotEqual(s1, s2);
        
        // Verify equals with null is false
        Assert.assertFalse(null == s1);
    }
    
    private void verifyNotEqual(SecurityContext s1, SecurityContext s2)
    {
        // Equals to one self
        Assert.assertTrue(s1.equals(s1));
        Assert.assertTrue(s2.equals(s2));
        
        // Not equal
        Assert.assertFalse(s1.equals(s2));
        Assert.assertFalse(s2.equals(s1));
        Assert.assertFalse(s2.equals(new Object()));
    }

}
