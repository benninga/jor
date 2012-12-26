package org.jor.rest.user;

import static org.jor.shared.api.rest.LoginResourceConstants.LOGIN_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.LOGOUT_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.NEW_USER_PATH;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jor.model.user.User;
import org.jor.model.user.UserInfo;
import org.jor.rest.RestConstants;
import org.jor.server.services.user.LoginFailedException;

public interface LoginResource
{

    @POST
    @Path(LOGIN_PATH)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, RestConstants.APPLICATION_JAVA })
    User postLogin(UserCredentials credentials) throws LoginFailedException;

    @POST
    @Path(LOGOUT_PATH)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, RestConstants.APPLICATION_JAVA })
    String postLogout();
    
    @Path(NEW_USER_PATH)
    @POST
    String setUpNewUser(UserInfo info);

}
