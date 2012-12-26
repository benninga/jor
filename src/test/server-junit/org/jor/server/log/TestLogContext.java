package org.jor.server.log;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.MDC;

public class TestLogContext
{
    private static final String FOO = "foo-";
    
    @Test
    public void testSetRemoveRemoteIP()
    {
        String key = LogContext.IP_ADDRESS;
        String value = FOO + Math.random();
        LogContext c = LogContext.get();
        c.setRemoteIP(value);
        validateValue(key, value);
        c.removeRemoteIP();
        validateValue(key, null);
    }
    
    @Test
    public void testSetRemoveUserName()
    {
        String key = LogContext.USER_NAME;
        String value = FOO + Math.random();
        LogContext c = LogContext.get();
        c.setUserName(value);
        validateValue(key, value);
        c.removeUserName();
        validateValue(key, null);
    }
    
    @Test
    public void testSetRemoveRequestId()
    {
        String key = LogContext.REQUEST_ID;
        String value = FOO + Math.random();
        LogContext c = LogContext.get();
        c.setRequestId(value);
        validateValue(key, value);
        c.removeRequestId();
        validateValue(key, null);
    }
    
    private static void validateValue(String key, String expected)
    {
        String actual = MDC.get(key);
        Assert.assertEquals("Same MDC Value", expected, actual);
    }
}
