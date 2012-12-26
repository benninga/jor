package org.jor.shared.log;

public interface Logger
{
    boolean isDebugEnabled();
    
    boolean isInfoEnabled();
    
    void debug(String message);
    
    void info(Object message);
    
    void info(String message);
    
    void warn(String message);
    
    void warn(Throwable error);
    
    void warn(String message, Throwable error);
    
    void error(String message);
    
    void error(Throwable error);
    
    void error(String message, Throwable error);
}
