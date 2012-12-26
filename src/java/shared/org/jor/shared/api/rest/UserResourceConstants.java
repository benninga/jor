package org.jor.shared.api.rest;

public interface UserResourceConstants extends CommonConstants
{
    String USER_PATH = "/user";
    String USER_URL = BASE_URL + USER_PATH;
    String HEADERS_PATH = "/headers";
    String HEADERS_URL = USER_URL + HEADERS_PATH;
    
    String SINGLE_USER_PATH = USER_PATH + "/single";
    String SINGLE_USER_URL = BASE_URL + SINGLE_USER_PATH;
    
    String CUSTOMER_PATH = "/customer";
    String CUSTOMER_URL = SINGLE_USER_URL + CUSTOMER_PATH;
    
    String UPDATE_CUSTOMER_PATH = CUSTOMER_PATH + "/update";
    String UPDATE_CUSTOMER_URL = CUSTOMER_URL + UPDATE_CUSTOMER_PATH;
    
    String PURCHASE_PATH = "/purchase";
    String INFO_PATH = "/info";
}
