package org.jor.server.services.security;

import static org.jor.shared.api.rest.LoginResourceConstants.AUTH_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.EULA_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.LOGIN_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.LOGOUT_PATH;
import static org.jor.shared.api.rest.LoginResourceConstants.NEW_USER_PATH;

import org.jor.shared.security.ActionConstants;

public interface Resources
{
    String REST = "REST.";
    String REST_LOGIN_RESOURCE = REST + AUTH_PATH + LOGIN_PATH;
    String REST_LOGOUT_RESOURCE = REST + AUTH_PATH + LOGOUT_PATH;
    String REST_EULA_RESOURCE = REST + AUTH_PATH + EULA_PATH;
    String REST_NEW_USER_RESOURCE = REST + AUTH_PATH + NEW_USER_PATH;

    String MODEL = ActionConstants.MODEL + ".";
}
