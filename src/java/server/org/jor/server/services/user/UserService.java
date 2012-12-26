package org.jor.server.services.user;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.jor.server.services.ServicesManager;
import org.jor.common.PasswordUtil;
import org.jor.model.user.User;
import org.jor.server.services.SessionManager;
import org.jor.server.services.db.DataService;
import org.jor.shared.AccessConstants;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class UserService
{
    public static final String ADMIN_USER_NAME = "admin";
    public static final String ADMIN_EMAIL = "admin@noemail.com";

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    
    private static UserService instance = new UserService();
    private static UserService defaultService;
    
    public static UserService get()
    {
        if(defaultService != null) {
            return defaultService;
        }
        return instance;
    }
    
    public static void set(UserService service)
    {
        defaultService = service;
    }
    
    public boolean isSessionValid(SecurityContext session)
    {
        return AccessUtil.get().isSessionValid(session);
    }
    
    public SecurityContext loginUser(String userName, String password)
    {
        User user = getUser(userName);
        if (user == null)
        {
            LOG.info("No such user: " + userName);
            return null;
        }
        else if (matchPassword(password, user.getPassword()) == false)
        {
            LOG.info("Authentication failed" + ": Provided password = " + password + ", User password = "
                    + user.getPassword());
            return null;
        }

        String sessionId = user.getSessionId();
        if (sessionId == AccessConstants.EMPTY_SESSION_ID)
        {
            sessionId = generateSessionId();
            user.setSessionId(sessionId);
        }

        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.WEEK_OF_YEAR, 1);
        Date expirationDate = expiration.getTime();
        user.setSessionExpiration(expirationDate);

        // Call the user service directly. We know that we are not making illegal
        // modifications to the user here.
        user = DataService.getDataService().save(user);
        String userKey = user.getKey().convert();

        SecurityContext session = new SecurityContext(userKey, userName, sessionId);
        LOG.info("Authenticated user '" + userName + "' successfully");
        return session;
    }
    
    /**
     * @return true if the user was logged out. False if user was not already logged in.
     */
    public boolean logoutUser()
    {
        SecurityContext session = SessionManager.get().getSession().getSecurityContext();
        if (isSessionValid(session) == false) {
            return false;
        }
        
        String username = session.getUsername();
        User user = getUser(session.getUsername());
        if (user == null)
        {
            LOG.info("No such user: " + username);
            return false;
        }
        
        user.setSessionId(null);
        user.setSessionExpiration(null);
        
        // Call the user service directly. We know that we are not making illegal
        // modifications to the user here.
        user = DataService.getDataService().save(user);

        LOG.info("Logged out user '" + username + "' successfully");
        return true;
    }

    public User saveUser(User user, DataService dataService) throws CreateUserException
    {

        User existingUser = getUser(user.getUsername(), dataService);
        if (existingUser != null)
        {
            if (user.getKey() == null)
            {
                // We found an existing user with the same name.
                throw new CreateUserException("User already exists with same user name: "
                        + user.getUsername());
            }
            else if (existingUser.getKey().equals(user.getKey()) == false)
            {
                // A user name is being changed to a user name that already exists.
                throw new CreateUserException("User already exists with same user name: "
                        + user.getUsername());
            }
        }

        return DataService.getDataService().save(user);
    }
    
    private User getUser(String userName)
    {
        return getUser(userName, DataService.getDataService());
    }
    
    private User getUser(String userName, DataService dataService)
    {
        User user = dataService.getSingleObject(User.class, User.FIELD_USERNAME, userName);
        if (user == null && ADMIN_USER_NAME.equals(userName))
        {
            user = new User();
            user.setName("Administrator");
            user.setUsername(ADMIN_USER_NAME);
            user.setEmail(ADMIN_EMAIL);
            user.setPassword(ADMIN_USER_NAME);
            user.setIsAdmin(true);
            user = dataService.save(user);
        }
        
        return user;
    }

    private boolean matchPassword(String password, String hashedPassword)
    {
        if (ServicesManager.isDebugMode()) {
            return true;
        }
        return PasswordUtil.matchPassword(password, hashedPassword);
    }

    private String generateSessionId()
    {
        UUID sessionId = UUID.randomUUID();
        return sessionId.toString();
    }
}
