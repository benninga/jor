package org.jor;

public interface LoginConstantsForTests
{
    String USER_ONE = System.getProperty("system.test.user1.name", "testUser1");
    String USER_ONE_PASSWORD = System.getProperty("system.test.user1.password", "password");
    String USER_TWO = System.getProperty("system.test.user2.name", "testUser2");
    String USER_TWO_PASSWORD = System.getProperty("system.test.user2.password", "password");
    
    String SYSTEM_USER_TWO = System.getProperty("system.test.systemuser.name", "systemTest");
    String SYSTEM_USER_TWO_PASSWORD = System.getProperty("system.test.systemuser.password", "password");
    
    String TEST_HOST = System.getProperty("system.test.host", "localhost");
    int TEST_PORT = Integer.parseInt(System.getProperty("system.test.port", "8888"));
    
    String LOCALHOST_IP = "127.0.0.1";
    int DEFAULT_STOP_PORT = 8079;
    String JETTY_HTTP_PORT_PROPERTY = "jetty.http.port";
    
    String DEFAULT_WAR_DIR = "war";
    String DEFAULT_CONTEXT_PATH = "/";
}
