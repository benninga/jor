package org.jor.server.log;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class AddLogRequestIdFilter implements Filter
{
    private static Logger logger = LoggerFactory.getLogger(AddLogRequestIdFilter.class);
    private static AtomicLong counter = new AtomicLong(0);
    
    private final int WARN_DELAY_THRESHOLD = 300;
    private final int ERROR_DELAY_THRESHOLD = 1000;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        long requestId = counter.addAndGet(1);
        LogContext.get().setRequestId(Long.toString(requestId));
        long start = System.currentTimeMillis();
        
        try
        {
            chain.doFilter(request, response);
        }
        catch (Exception error)
        {
            logger.error(error);
        }
        finally
        {
            long total = System.currentTimeMillis() - start;
            logRequest(request, total, requestId);
            LogContext.get().removeRequestId();
        }
    }
    
    private void logRequest(ServletRequest request, long totalTime, long requestId)
    {
        String servletName = ((HttpServletRequest)request).getRequestURI();
        String httpMethod = ((HttpServletRequest)request).getMethod();

        String logMessage = String.format("Served request %d for path %s:%s in (ms)%d",
                                  requestId, httpMethod, servletName, totalTime);
        
        try
        {
            UserActivityLogger.log(httpMethod, servletName, totalTime);
        }
        catch (Exception e)
        {
            logger.error("Failed to save user activity log", e);
        }
        
        if (totalTime <= WARN_DELAY_THRESHOLD)
        {
            logger.info(logMessage);
        }
        else if (totalTime <= ERROR_DELAY_THRESHOLD)
        {
            logger.warn(logMessage);
        }
        else 
        {
            logger.error(logMessage);
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig)
    {
        logger.debug("Log Request Id Filter, Initializing filter...");
        // For now, do nothing.
    }

    @Override public void destroy()
    {
        logger.debug("Log Request Id Filter, Destroying filter...");
        // Do nothing for now.
    }
}
