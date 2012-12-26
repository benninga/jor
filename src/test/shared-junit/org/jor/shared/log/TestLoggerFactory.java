package org.jor.shared.log;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLoggerFactory
{
    private LoggerProvider lastProvider;
    
    @Before public void setUp()
    {
        lastProvider = LoggerFactory.getLoggerProvider();
    }
    
    @After public void tearDown()
    {
        LoggerFactory.setLoggerProvider(lastProvider);
    }
    
    @Test public void testEmptyConstructor()
    {
        // We don't use the empty constructor but it exists and we'd like
        // to indicate that we covered it.
        new LoggerFactory();
    }
    
    @Test public void testLoggerProvider()
    {
        LoggerFactory.setLoggerProvider(null);
        Logger logger = LoggerFactory.getLogger(TestLoggerFactory.class);
        Assert.assertEquals("Simple Logger", SimpleLogger.class.getName(), logger.getClass().getName());
        
        LoggerFactory.setLoggerProvider(new MockLoggerProvider());
        logger = LoggerFactory.getLogger(TestLoggerFactory.class);
        Assert.assertEquals("Java Logger", MockLoggerProvider.MockLogger.class.getName(),
                            logger.getClass().getName());
    }
}
