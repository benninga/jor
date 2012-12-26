package org.jor.server.services.db;

import junit.framework.Assert;

import org.junit.Test;

public class TestInvalidObjectException
{
    @Test public void testConstructors()
    {
        String message = "My message";
        Exception e = new InvalidObjectException(message);
        Assert.assertEquals("Message is the same", message, e.getMessage());
        
        Exception cause = new Exception("Cause");
        e = new InvalidObjectException(cause);
        Assert.assertTrue("Message is the same", cause == e.getCause());
    }

}
