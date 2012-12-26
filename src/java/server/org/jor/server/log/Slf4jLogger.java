package org.jor.server.log;

import org.jor.shared.log.Logger;

public class Slf4jLogger implements Logger
{
    private org.slf4j.Logger logger;
    
    public Slf4jLogger(org.slf4j.Logger logger)
    {
        this.logger = logger;
    }
    
    @Override
    public void debug(String message)
    {
        logger.debug(message);
    }

    @Override
    public void info(Object message)
    {
        if(message == null) {
            logger.info("null");
        }
        else {
            logger.info(message.toString());
        }
    }

    @Override
    public void info(String message)
    {
        logger.info(message);
    }

    @Override
    public void warn(String message)
    {
        logger.warn(message);
    }

    @Override
    public void warn(Throwable error)
    {
        logger.warn(error.getMessage(), error);
    }

    @Override
    public void warn(String message, Throwable error)
    {
        logger.warn(message, error);
    }

    @Override
    public void error(String message)
    {
        logger.error(message);
    }

    @Override
    public void error(Throwable error)
    {
        logger.error(error.getLocalizedMessage(), error);
    }

    @Override
    public void error(String message, Throwable error)
    {
        logger.error(message, error);
    }

    @Override
    public boolean isDebugEnabled()
    {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled()
    {
        return logger.isInfoEnabled();
    }
}
