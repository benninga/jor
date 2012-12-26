package org.jor.server.log;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jor.server.services.SessionManager;
import org.jor.server.services.user.SecurityContext;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class AddLogContextFilter implements Filter
{
    private static Logger logger = LoggerFactory.getLogger(AddLogContextFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        try
        {
            SecurityContext sc = SessionManager.get().getSession().getSecurityContext();
            String userSignature = sc.getUsername();
            if (sc.getRunAsUserKey() != null)
            {
                userSignature = sc.getUsername() + " AS " + sc.getRunAsUsername();
            }
            LogContext.get().setUserName(userSignature);
            LogContext.get().setRemoteIP(request.getRemoteAddr());
            if (request instanceof HttpServletRequest)
            {
                HttpServletRequest http = (HttpServletRequest)request;
                LogContext.get().setUserAgent(http.getHeader(LogContext.HTTP_USER_AGENT));
                LogContext.get().setReferrer(http.getHeader(LogContext.HTTP_REFERRER));
                LogContext.get().setOrigin(http.getHeader(LogContext.HTTP_ORIGIN));
                LogContext.get().setUrl(http.getRequestURI());
                LogContext.get().setHttpMethod(http.getMethod());
            }
            chain.doFilter(request, response);
        }
        finally
        {
            LogContext.get().removeUserName();
            LogContext.get().removeRemoteIP();
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig)
    {
        logger.debug("Log Context Filter, Initializing filter...");
        // For now, do nothing.
    }

    @Override public void destroy()
    {
        logger.debug("Log Context Filter, Destroying filter...");
        // Do nothing for now.
    }
}
