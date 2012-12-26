package org.jor.shared.api.rest;


public interface EchoResourceConstants extends CommonConstants
{
    public static final String ECHO_PATH = "/echo";
    public static final String ECHO_URL = BASE_URL + ECHO_PATH;
    
    public static final String DOWNLOAD_TEST_PATH = "/downloadtest";
    public static final String PING_TEST_PATH = "/pingtest";
    
    public static final String DOWNLOAD_TEST_URL = ECHO_URL + DOWNLOAD_TEST_PATH;
    public static final String PING_TEST_URL = ECHO_URL + PING_TEST_PATH;
}
