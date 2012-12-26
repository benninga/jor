package org.jor.shared.log;

public class MockLoggerProvider implements LoggerProvider
{
    @Override
    public Logger getLogger(Class<?> clazz)
    {
        return new MockLogger();
    }

    public static class MockLogger implements Logger
    {
        @Override
        public boolean isDebugEnabled()
        {
            return false;
        }

        @Override
        public boolean isInfoEnabled()
        {
            return false;
        }

        @Override
        public void debug(String message)
        {
            // Empty on purpose
        }

        @Override
        public void info(Object message)
        {
            // Empty on purpose
        }

        @Override
        public void info(String message)
        {
            // Empty on purpose
        }

        @Override
        public void warn(String message)
        {
            // Empty on purpose
        }

        @Override
        public void warn(Throwable error)
        {
            // Empty on purpose
        }

        @Override
        public void warn(String message, Throwable error)
        {
            // Empty on purpose
        }

        @Override
        public void error(String message)
        {
            // Empty on purpose
        }

        @Override
        public void error(Throwable error)
        {
            // Empty on purpose
        }

        @Override
        public void error(String message, Throwable error)
        {
            // Empty on purpose
        }
        
    }
}
