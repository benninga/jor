package org.jor.shared.api.rest;

public interface LoginResourceConstants extends CommonConstants
{
    String AUTH_PATH = "/auth";
    String LOGIN_PATH = "/login";
    String LOGOUT_PATH ="/logout";
    String EULA_PATH = LOGIN_PATH + "/eula";
    String DOWNLOAD_TEST_PATH = "/downloadtest";
    String PING_TEST_PATH = "/pingtest";
    String NEW_USER_PATH = "/newuser";
    
    String AUTH_URL = BASE_URL + AUTH_PATH;
    String LOGIN_URL = AUTH_URL + LOGIN_PATH;
    String FIRST_LOGIN_URL = AUTH_URL + EULA_PATH;
    String LOGOUT_URL = AUTH_URL + LOGOUT_PATH;
    String DOWNLOAD_TEST_URL = AUTH_URL + DOWNLOAD_TEST_PATH;
    String PING_TEST_URL = AUTH_URL + PING_TEST_PATH;
    String NEW_USER_URL = AUTH_URL + NEW_USER_PATH;
}
