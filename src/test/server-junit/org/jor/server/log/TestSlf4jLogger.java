package org.jor.server.log;

import java.util.Date;

import org.junit.Test;

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerProvider;
import org.jor.shared.log.TestSimpleLogger;

public class TestSlf4jLogger
{
    @Test public void testSimpleLogger()
    {
        // Verify all methods can be invoked.
        Logger logger = new Slf4jLogger(org.slf4j.LoggerFactory.getLogger("Logger-" + Math.random()));
        TestSimpleLogger.invokeLoggerMethods(logger);
        logger.info((Date)null);
        logger.info(new Date());
    }
    
    @Test public void testSlf4jLoggerProvider()
    {
        LoggerProvider provider = new Slf4jLoggerProvider();
        Logger logger = provider.getLogger(TestSlf4jLogger.class);
        TestSimpleLogger.invokeLoggerMethods(logger);
    }
}
