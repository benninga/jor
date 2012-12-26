package org.jor.shared.log;

public interface LoggerProvider
{
    public Logger getLogger(Class<?> clazz);
}
