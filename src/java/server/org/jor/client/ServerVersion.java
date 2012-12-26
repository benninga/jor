package org.jor.client;

import java.io.InputStream;
import java.util.Properties;

public class ServerVersion
{
    private static final String D = ".";
    private static final String MAJOR;
    private static final String MINOR;
    private static final String PATCH;
    private static final String BUILD;
    private static final String VERSION;

    static
    {
        String versionFile = "VersionConstants.properties";
        try
        {
            InputStream input = ServerVersion.class.getResourceAsStream(versionFile);
            Properties properties = new Properties();
            properties.load(input);
            MAJOR = properties.getProperty("majorNumber");
            MINOR = properties.getProperty("minorNumber");
            PATCH = properties.getProperty("patchNumber");
            BUILD = properties.getProperty("buildNumber");
            VERSION = MAJOR + D + MINOR + D + PATCH + D + BUILD;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to load version file: " + versionFile);
        }
    }
    

    public static String getMajor()
    {
        return MAJOR;
    }

    public static String getMinor()
    {
        return MINOR;
    }

    public static String getPatch()
    {
        return PATCH;
    }

    public static String getBuild()
    {
        return BUILD;
    }

    public static String getVersion()
    {
        return VERSION;
    }

}
