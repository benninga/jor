package org.jor.shared.log;

import java.util.Date;

import org.junit.Test;

public class TestSimpleLogger
{
    @Test public void testSimpleLogger()
    {
        // Verify all methods can be invoked.
        SimpleLogger logger = new SimpleLogger();
        
        logger.setDebugEnabled(false);
        logger.setInfoEnabled(false);
        invokeLoggerMethods(logger);
        
        logger.setDebugEnabled(true);
        logger.setInfoEnabled(true);
        invokeLoggerMethods(logger);
    }
    
    public static void invokeLoggerMethods(Logger logger)
    {
        Exception error = new Exception("An exception for usage in a logger message. Don't panic :-)");
        logger.isDebugEnabled();
        logger.isInfoEnabled();
        logger.debug("Hello-1");
        logger.info("Hello-2");
        logger.info(new Date());
        logger.warn("Hello-3");
        logger.warn(error);
        logger.warn("Hello", error);
        logger.error("Hello-4");
        logger.error(error);
        logger.error("Hello-5", error);
    }
}
