package org.jor.server.log;

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerProvider;

public class Slf4jLoggerProvider implements LoggerProvider
{
    @Override
    public Logger getLogger(Class<?> clazz)
    {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(clazz.getName()));
    }

}
