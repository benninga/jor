package org.jor.server.services.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Assert;
import org.junit.Test;

public class TestTransaction
{
    @Test
    public void testCorrectExceptionHandling()
    {
        Transaction t = new Transaction(TestPersistenceManager.createSession(true));
        t.begin();
        try
        {
            t.commit();
            Assert.fail("Invalid object exception should have been thrown");
        }
        catch (InvalidObjectException e)
        {
            // This is expected.
        }
    }
    
    @Test
    public void testBasicDelegation()
    {
        Transaction t = new Transaction(TestPersistenceManager.createSession(false));
        MyTransaction internal = (MyTransaction)Proxy.getInvocationHandler(t.getInternalTransaction());
        
        Assert.assertFalse(internal.beginCalled);
        t.begin();
        Assert.assertTrue("Begin was not called", internal.beginCalled);
        
        Assert.assertFalse(internal.commitCalled);
        t.commit();
        Assert.assertTrue("Commit was not called", internal.commitCalled);
        
        Assert.assertFalse(internal.rollbackCalled);
        t.rollback();
        Assert.assertTrue("Rollback was not called", internal.rollbackCalled);
        
        
    }
    
    static class MyTransaction implements InvocationHandler
    {
        boolean beginCalled = false;
        boolean commitCalled = false;
        boolean rollbackCalled = false;
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String methodName = method.getName();
            if ("begin".equals(methodName))
            {
                this.beginCalled = true;
                return null;
            }
            else if ("commit".equals(methodName))
            {
                this.commitCalled = true;
                return null;
            }
            else if ("rollback".equals(methodName))
            {
                this.rollbackCalled = true;
                return null;
            }
            throw new RuntimeException("Unimplemented method: " + methodName);
        }
    }
}
