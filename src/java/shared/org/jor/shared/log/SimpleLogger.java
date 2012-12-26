package org.jor.shared.log;


public class SimpleLogger implements Logger
{
    private static boolean debugEnabled = false;
    private static boolean infoEnabled = true;

    public void setDebugEnabled(boolean enabled)
    {
        SimpleLogger.debugEnabled = enabled;
    }

    @Override
    public boolean isDebugEnabled()
    {
        return debugEnabled;
    }

    public void setInfoEnabled(boolean enabled)
    {
        SimpleLogger.infoEnabled = enabled;
    }

    @Override
    public boolean isInfoEnabled()
    {
        return infoEnabled;
    }
    
    @Override
    public void debug(String message)
    {
        if(isDebugEnabled()) {
            out(message);
        }
    }
    
    @Override
    public void info(Object message)
    {
        if(isInfoEnabled())
        {
            if(message == null) {
                out("null");
            }
            else {
                out(message.toString());
            }
        }
    }
    
    @Override
    public void info(String message)
    {
        if(isInfoEnabled()) {
            out(message);
        }
    }
    
    @Override
    public void error(Throwable error)
    {
        err(error);
    }

    @Override
    public void error(String message)
    {
        err(message);
    }

    @Override
    public void error(String message, Throwable error)
    {
        err(message);
        err(error);
    }

    @Override
    public void warn(String message)
    {
        err(message);
    }

    @Override
    public void warn(Throwable error)
    {
        err(error);
    }

    @Override
    public void warn(String message, Throwable error)
    {
        err(message);
        err(error);
    }
    
    protected void out(String message)
    {
        System.out.print("[Simple Logger] ");
        System.out.println(message);
    }
    
    protected void err(Throwable error)
    {
        error.printStackTrace();
    }
    
    protected void err(String message)
    {
        System.err.print("[Simple Logger] ");
        System.err.println(message);
    }
}
