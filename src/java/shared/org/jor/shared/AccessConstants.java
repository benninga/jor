package org.jor.shared;

public interface AccessConstants
{
    public static final String SESSION_ID_COOKIE = "SessionId";
    public static final String USER_KEY_COOKIE = "UserKey";
    public static final String USER_NAME_COOKIE = "Username";
    public static final String RUN_AS_USER_KEY_COOKIE = "RunAsUserKey";
    public static final String RUN_AS_USER_NAME_COOKIE = "RunAsUsername";
    public static final String CLIENT_VERSION_COOKIE = "ClientVersion";
    
    public static final String VERSION_MISMATCH_HEADER = "Version-Mismatch";
    
    public static final String NO_SESSION_VALUE = "NoSession";
    public static final String EMPTY_SESSION_ID = null;

    // login duration is 2 weeks in this example.
    public static final int SESSION_DURATION_SECONDS = 60 * 60 * 24 * 14;
    public static final int SESSION_DURATION_MILLIS = 1000 * SESSION_DURATION_SECONDS;
}
