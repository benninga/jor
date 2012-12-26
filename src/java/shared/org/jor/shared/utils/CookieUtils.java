package org.jor.shared.utils;

import java.util.Date;

public class CookieUtils
{
    // milliseconds from 1970 to some time in 2029
    public static final Long COOKIE_EXPIRATION_TIME = 1876800000000L;
    public static final Integer PERMANENT_COOKIE_MAX_AGE = 550000000;
    
    public static final Date getPermanentCookieExpirationDate()
    {
        Date expires = new Date();
        expires.setTime(COOKIE_EXPIRATION_TIME);
        return expires;
    }
}
