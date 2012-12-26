package org.jor.shared.log;

public class LoggerFactory
{
    private static final Logger logger = new SimpleLogger();
    private static LoggerProvider loggerProvider;
    
    public static Logger getLogger(Class<?> clazz)
    {
        if(loggerProvider != null)
        {
            return loggerProvider.getLogger(clazz);
        }
        return logger;
    }
    
    public static synchronized LoggerProvider getLoggerProvider()
    {
        return loggerProvider;
    }
    
    public static synchronized void setLoggerProvider(LoggerProvider provider)
    {
        loggerProvider = provider;
    }
}
