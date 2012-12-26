package org.jor.rest.user;

import static org.jor.shared.api.rest.LoginResourceConstants.AUTH_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.EULA_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.LOGIN_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.LOGOUT_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.NEW_USER_PATH;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jor.model.user.User;
import org.jor.model.user.UserInfo;
import org.jor.rest.RestConstants;
import org.jor.server.rest.FreeMarkerHelper;
import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectKey;
import org.jor.server.services.user.CreateUserException;
import org.jor.server.services.user.LoginFailedException;
import org.jor.server.services.user.SecurityContext;
import org.jor.server.services.user.UserService;
import org.jor.shared.AccessConstants;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import org.jor.shared.utils.CookieUtils;

@Path(AUTH_PATH)
public class LoginResourceImpl implements RestConstants, LoginResource
{
    private static final String ROOT = "/";

    private static final Logger LOG = LoggerFactory.getLogger(LoginResourceImpl.class);
    
    @Context
    private HttpHeaders httpHeaders;
    @Context
    private HttpServletResponse httpResponse;

    @GET
    @Path(LOGIN_PATH)
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public Response getLoginHtml()
    {
        return getLoginHtml("");
    }
    
    private Response getLoginHtml(String errorMessage)
    {
        SecurityContext session = getSecurityContext();
        boolean validSession = UserService.get().isSessionValid(session);
        if (validSession)
        {
            URI logoutURI = getURI(LOGOUT_PATH);
            return Response.seeOther(logoutURI).entity("User already authenticated").build();
        }
        
        // Give a login page if user not authenticated
        Map<String, Object> root = new HashMap<>();
        root.put("errorMessage", errorMessage);
        String page = FreeMarkerHelper.processTemplateFile(LOGIN_PAGE_FTL, root);
        return Response.ok()
                       .cookie(expiredCookie(AccessConstants.SESSION_ID_COOKIE))
                       .cookie(expiredCookie(AccessConstants.USER_KEY_COOKIE))
                       .cookie(expiredCookie(AccessConstants.USER_NAME_COOKIE))
                       .entity(page).build();
    }
    
    @POST
    @Path(LOGIN_PATH)
    @Produces(MediaType.TEXT_HTML)
    public Response postLoginHtml(@FormParam(User.FIELD_USERNAME) String username,
                                  @FormParam(User.FIELD_PASSWORD) String password)
    {
        SecurityContext session = UserService.get().loginUser(username, password);
        if (session == null) {
            return getLoginHtml("Username or password is incorrect");
        }

        LOG.info("Successfully authenticated user: " + username);
        
        URI logoutURI = getURI(LOGOUT_PATH);
        return Response.seeOther(logoutURI)
                       .cookie(newCookie(AccessConstants.SESSION_ID_COOKIE, session.getSessionId() ))
                       .cookie(newCookie(AccessConstants.USER_KEY_COOKIE, session.getUserKey()))
                       .cookie(newCookie(AccessConstants.USER_NAME_COOKIE, session.getUsername()))
                       .entity("Authentication Successful").build();
    }
    
    @POST
    @Path(EULA_PATH)
    public User postFirstLogin(String userKeyString)
    {
        ObjectKey userKey = new ObjectKey(userKeyString);
        DataService service = DataService.getDataService();
        User user = service.getObject(User.class, userKey);
        user.setIsEulaAccepted(true);
        service.save(user);
        return user;
    }

    /* (non-Javadoc)
     * @see org.jor.rest.user.LoginResource#postLogin(org.jor.rest.user.UserCredentials)
     */
    @Override
    @POST
    @Path(LOGIN_PATH)
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, APPLICATION_JAVA})
    public User postLogin(UserCredentials credentials) throws LoginFailedException
    {
        SecurityContext session =
            UserService.get().loginUser(credentials.getUsername(), credentials.getPassword());
        if (session == null) {
            throw new LoginFailedException("Username or password incorrect", credentials.getUsername());
        }

        if (httpResponse != null)
        {
            httpResponse.addCookie(createHttpCookie(AccessConstants.SESSION_ID_COOKIE, session.getSessionId()));
            httpResponse.addCookie(createHttpCookie(AccessConstants.USER_KEY_COOKIE, session.getUserKey()));
            httpResponse.addCookie(createHttpCookie(AccessConstants.USER_NAME_COOKIE, session.getUsername()));
        }
        
        ObjectKey userKey = new ObjectKey(session.getUserKey());
        User user = DataService.getDataService().getObject(User.class, userKey);
        return user;
    }
    
    private javax.servlet.http.Cookie createHttpCookie(String name, String value)
    {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(CookieUtils.PERMANENT_COOKIE_MAX_AGE);
        return cookie;
    }

    @GET
    @Path(LOGOUT_PATH)
    @Produces(MediaType.TEXT_HTML)
    public Response getLogoutHtml()
    {
        SecurityContext session = getSecurityContext();
        boolean validSession = UserService.get().isSessionValid(session);
        // Give a login page if user not authenticated
        if (validSession == false)
        {
            // Let's make sure to also remove the cookies
            URI logoutURI = getURI(LOGIN_PATH);
            return Response.seeOther(logoutURI)
                           .entity("User is not authenticated")
                           .cookie(expiredCookie(AccessConstants.SESSION_ID_COOKIE))
                           .cookie(expiredCookie(AccessConstants.USER_KEY_COOKIE))
                           .cookie(expiredCookie(AccessConstants.USER_NAME_COOKIE))
                           .build();
        }
        
        Map<String, Object> root = new HashMap<>();
        root.put(AccessConstants.USER_NAME_COOKIE, session.getUsername());
        root.put(AccessConstants.USER_KEY_COOKIE, session.getUserKey());
        root.put(AccessConstants.SESSION_ID_COOKIE, session.getSessionId());
        
        String page = FreeMarkerHelper.processTemplateFile(LOGOUT_PAGE_FTL, root);
        return Response.ok().entity(page).build();
    }
    
    @POST
    @Path(LOGOUT_PATH)
    @Produces(MediaType.TEXT_HTML)
    public Response postLogoutHtml() 
    {
        String entity = postLogout();
        URI logoutURI = getURI(LOGIN_PATH);
        return Response.seeOther(logoutURI).entity(entity).build();
    }
    
    private NewCookie expiredCookie(String cookieName)
    {
        return new NewCookie(cookieName, null, ROOT, null, null, 0, false);
    }
    
    private NewCookie newCookie(String cookieName, String cookieValue)
    {
        return new NewCookie(cookieName, cookieValue, ROOT, null, null, CookieUtils.PERMANENT_COOKIE_MAX_AGE, false);
    }
    
    /* (non-Javadoc)
     * @see org.jor.rest.user.LoginResource#postLogout()
     */
    @Override
    @POST
    @Path(LOGOUT_PATH)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, RestConstants.APPLICATION_JAVA })
    public String postLogout()
    {
        UserService.get().logoutUser();
        return "User is logged out";
    }
    
    @Override
    @Path(NEW_USER_PATH)
    @POST
    public String setUpNewUser(UserInfo info)
    {
        User user = new User();
        user.updateFromInfo(info);
        DataService dataService = DataService.getDataService();
        
        try
        {
            dataService.begin();
            UserService.get().saveUser(user, dataService);
            dataService.commit();

            
            return "User created";
        }
        catch (CreateUserException e)
        {
            dataService.rollback();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    protected URI getURI(String page)
    {
        UriBuilder ub = UriBuilder.fromResource(this.getClass());
        URI uri = ub.path(page).build();
        return uri;
    }
    
    private Map<String, String> getCookies()
    {
        Map<String, String> cookies = new HashMap<>();
        Collection<Cookie> allCookies = httpHeaders.getCookies().values();
        for (Cookie c : allCookies)
        {
            cookies.put(c.getName(), c.getValue());
        }
        return cookies;
    }
    
    public SecurityContext getSecurityContext()
    {
        Map<String, String> cookies = getCookies();
        String userKey = null;
        String userName = null;
        String sessionId = null;
        
        for (Map.Entry<String, String> entry : cookies.entrySet())
        {
            String name = entry.getKey();
            String value = entry.getValue();
            if (name.equals(AccessConstants.USER_KEY_COOKIE))
            {
                userKey = value;
            }
            else if (name.equals(AccessConstants.USER_NAME_COOKIE))
            {
                userName = value;
            }
            else if (name.equals(AccessConstants.SESSION_ID_COOKIE))
            {
                sessionId = value;
            }
        }
        
        SecurityContext session = null;
        if (null != userKey && null != userName && null != sessionId
                && !AccessConstants.NO_SESSION_VALUE.equals(sessionId))
        {
            session = new SecurityContext(userKey, userName, sessionId);
        }
        return session;
    }
}
