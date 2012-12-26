package org.jor.server.services.user;

import org.jor.model.user.User;
import org.jor.server.services.db.DataService;
import org.jor.shared.AccessConstants;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AccessUtil
{
    private static final Logger logger = Logger.getLogger(AccessUtil.class.getName());
    private static final Set<AccessListener> listeners = new HashSet<>();

    private static AccessUtil instance = new AccessUtil();
    private static AccessUtil defaultAccessUtil;

    public static AccessUtil get()
    {
        if (defaultAccessUtil != null)
        {
            return defaultAccessUtil;
        }
        return instance;
    }

    public static void set(AccessUtil accessUtil)
    {
        defaultAccessUtil = accessUtil;
    }

    public boolean addListener(AccessListener listener)
    {
        return listeners.add(listener);
    }

    public static boolean removeListener(AccessListener listener)
    {
        return listeners.remove(listener);
    }

    public void removeAllListeners()
    {
        listeners.clear();
    }

    private User getUser(String userName)
    {
        DataService service = DataService.getDataService();
        User user = service.getSingleObject(User.class, User.FIELD_USERNAME, userName);
        return user;
    }

    public void verifyValidSession(SecurityContext session) throws NoAccessException
    {
        for (AccessListener listener : listeners)
        {
            listener.verifyValidSession(session);
        }

        if (session == null)
        {
            String message = "Client provided a null session object";
            logger.warning(message);
            throw new NoAccessException(message);
        }

        User user = getUser(session.getUsername());
        if (user == null)
        {
            String message = "Unknown user: " + session.getUsername();
            logger.warning(message);
            throw new NoAccessException(message);
        }

        boolean isSessionValid = isSessionValid(user, session);
        if (isSessionValid == false)
        {
            String message = "Session expired. Please login again.";
            logger.warning(message);
            throw new NoAccessException(message);
        }
    }

    public boolean isSessionValid(SecurityContext session)
    {
        for (AccessListener listener : listeners)
        {
            if (listener.isSessionValid(session) == false)
            {
                return false;
            }
        }

        if (session == null)
        {
            return false;
        }

        User user = getUser(session.getUsername());
        return isSessionValid(user, session);
    }

    private boolean isSessionValid(User user, SecurityContext session)
    {
        String sessionId = session.getSessionId();
        String userName = session.getUsername();
        String userKey = session.getUserKey();

        if (sessionId == null || sessionId.length() == 0)
        {
            return false;
        }
        if (userKey == null || userKey.length() == 0)
        {
            return false;
        }
        if (userName == null || userName.length() == 0)
        {
            return false;
        }

        if (user == null)
        {
            return false;
        }

        if (sessionId.equals(user.getSessionId()) == false)
        {
            return false;
        }

        Date expirationDate = user.getSessionExpiration();
        if (expirationDate != null)
        {
            Date today = new Date();
            if (today.after(expirationDate))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isSessionValid(User user, String reqUserName, String reqSessionId)
    {

        if (reqSessionId == null || reqSessionId.length() == 0)
        {
            return false;
        }

        if (reqUserName == null || reqUserName.length() == 0)
        {
            return false;
        }

        if (user == null)
        {
            return false;
        }

        if (reqSessionId.equals(user.getSessionId()) == false)
        {
            return false;
        }

        Date expirationDate = user.getSessionExpiration();
        if (expirationDate != null)
        {
            Date today = new Date();
            if (today.after(expirationDate))
            {
                return false;
            }
        }
        return true;
    }

    public boolean isCurrentSessionValid(HttpServletRequest request)
    {
        String sessionId = "NoSession";
        String userName = null;
        Cookie cookie[] = request.getCookies();
        if (null != cookie)
        {
            for (int index = 0; index < cookie.length; index++)
            {
                if (cookie[index].getName().equals(AccessConstants.SESSION_ID_COOKIE))
                {
                    sessionId = cookie[index].getValue();
                }
                else if (cookie[index].getName().equals(AccessConstants.USER_NAME_COOKIE))
                {
                    userName = cookie[index].getValue();
                }
            }
        }
        User user = getUser(userName);
        return isSessionValid(user, userName, sessionId);
    }

    public String getUserName(HttpServletRequest request)
    {
        String userName = null;
        String sessionId = "NoSession";
        Cookie cookie[] = request.getCookies();
        if (null != cookie)
        {
            for (int index = 0; index < cookie.length; index++)
            {
                if (cookie[index].getName().equals(AccessConstants.SESSION_ID_COOKIE))
                {
                    sessionId = cookie[index].getValue();
                }
                
                if (cookie[index].getName().equals(AccessConstants.USER_NAME_COOKIE))
                {
                    userName = cookie[index].getValue();
                }
            }
        }
        
        User user = getUser(userName);
        boolean isValidSession = isSessionValid(user, userName, sessionId);
        if (!isValidSession)
        {
            return null;
        }
        return userName;
    }

    public String getSessionId(HttpServletRequest request)
    {
        String sessionId = "NoSession";
        String userName = null;
        Cookie cookie[] = request.getCookies();
        if (null != cookie)
        {
            for (int index = 0; index < cookie.length; index++)
            {
                if (cookie[index].getName().equals(AccessConstants.SESSION_ID_COOKIE))
                {
                    sessionId = cookie[index].getValue();
                }
                
                if (cookie[index].getName().equals(AccessConstants.USER_NAME_COOKIE))
                {
                    userName = cookie[index].getValue();
                }
            }
        }
        
        User user = getUser(userName);
        boolean isValidSession = isSessionValid(user, userName, sessionId);
        if (!isValidSession)
        {
            return AccessConstants.NO_SESSION_VALUE;
        }
        return sessionId;
    }
    
}
