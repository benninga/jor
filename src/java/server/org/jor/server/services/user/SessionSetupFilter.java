package org.jor.server.services.user;

import org.jor.client.ServerVersion;
import org.jor.server.services.SessionManager;
import org.jor.shared.AccessConstants;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionSetupFilter implements Filter
{
    private static final String ROOT = "/";
    private static Logger logger = LoggerFactory.getLogger(SessionSetupFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        try
        {
            setupSession(request, response);
            chain.doFilter(request, response);
            updateVersionVerification(request, response);
        }
        finally
        {
            removeSession();
        }
    }

    protected void setupSession(ServletRequest request, ServletResponse response)
    {
        SessionManager manager = SessionManager.get();
        manager.removeSession();
        
        boolean isSessionValid = false;
        if (request instanceof HttpServletRequest == false)
        {
            return;
        }

        SecurityContext sc = getSecurityContext(((HttpServletRequest)request).getCookies());
        isSessionValid = UserService.get().isSessionValid(sc);
        if (isSessionValid)
        {
            manager.createSession(sc);
        }
        else
        {
            clearCookies(response);
            manager.createSession();
        }
    }
    
    public SecurityContext getSecurityContext(Cookie[] allCookies)
    {
        String userKey = null;
        String userName = null;
        String sessionId = null;
        String runAsKey = null;
        String runAsUserName = null;
        if (null != allCookies)
        {
            for (Cookie c : allCookies)
            {
                if (AccessConstants.USER_KEY_COOKIE.equals(c.getName()))
                {
                    userKey = getCookieValue(c);
                }
                else if (AccessConstants.USER_NAME_COOKIE.equals(c.getName()))
                {
                    userName = getCookieValue(c);
                }
                else if (AccessConstants.SESSION_ID_COOKIE.equals(c.getName()))
                {
                    sessionId = getCookieValue(c);
                }
                else if (AccessConstants.RUN_AS_USER_KEY_COOKIE.equals(c.getName()))
                {
                    runAsKey = getCookieValue(c);
                }
                else if (AccessConstants.RUN_AS_USER_NAME_COOKIE.equals(c.getName()))
                {
                    runAsUserName = getCookieValue(c);
                }
            }
        }
        SecurityContext session = null;
        if (null != userKey && null != userName && null != sessionId
                && !AccessConstants.NO_SESSION_VALUE.equals(sessionId))
        {
            session = new SecurityContext(userKey, userName, sessionId);
            if (null != runAsKey && null != runAsUserName)
            {
                session = new SecurityContext(userKey, userName, sessionId, runAsKey, runAsUserName);
            }
        }
        
        return session;
    }
    
    private String getCookieValue(Cookie c)
    {
        if (c.getValue() == null || "null".equals(c.getValue()))
        {
            return null;
        }
        else
        {
            return c.getValue();
        }
    }
    
    private void clearCookies(ServletResponse response)
    {
        if (response instanceof HttpServletResponse == false) {
            return;
        }
        HttpServletResponse r = (HttpServletResponse)response;
        Cookie c;
        c = new Cookie(AccessConstants.SESSION_ID_COOKIE, null);
        c.setPath(ROOT);
        c.setMaxAge(0);
        r.addCookie(c);
        c = new Cookie(AccessConstants.USER_KEY_COOKIE, null);
        c.setPath(ROOT);
        c.setMaxAge(0);
        r.addCookie(c);
        c = new Cookie(AccessConstants.USER_NAME_COOKIE, null);
        c.setPath(ROOT);
        c.setMaxAge(0);
        r.addCookie(c);
    }
    
    protected void removeSession()
    {
        SessionManager.get().removeSession();
    }

    
    @Override
    public void init(FilterConfig filterConfig)
    {
        logger.debug("SessionSetupFilter, Initializing filter...");
        // For now, do nothing.
    }

    @Override public void destroy()
    {
        logger.debug("SessionSetupFilter, Destroying filter...");
        // Do nothing for now.
    }
    
    private void updateVersionVerification(ServletRequest _request, ServletResponse _response)
    {
        if ((_request instanceof HttpServletRequest == false) || (_response instanceof HttpServletResponse == false))
        {
            return;
        }
        
        HttpServletRequest request = (HttpServletRequest)_request;
        HttpServletResponse response = (HttpServletResponse)_response;
        
        String clientVersion = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies)
        {
            if (AccessConstants.CLIENT_VERSION_COOKIE.equals(cookie.getName()))
            {
                clientVersion = cookie.getValue();
                break;
            }
        }
        
        if (clientVersion == null || clientVersion.isEmpty()) {
            return;
        }
        
        if (ServerVersion.getVersion().equals(clientVersion) == false)
        {
            response.setHeader(AccessConstants.VERSION_MISMATCH_HEADER, ServerVersion.getVersion());
        }
    }

}
