package org.jor.server.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;

import org.jor.client.ServerVersion;
import org.jor.server.log.LogContext;
import org.jor.server.log.Slf4jLoggerProvider;
import org.jor.server.services.db.DataService;
import org.jor.server.services.user.SecurityContext;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import com.google.common.collect.Sets;

public class ServicesManager implements ServletContextListener
{
    public static boolean createDefaultContent = true;

    private static Logger LOG;
    
    public enum AWS_METADATA_RESOURCE
    {
        INSTANCE_ID("/instance-id", "Unknown Instance Id"),
        PUBLIC_HOSTNAME("/public-hostname", "Unknown Public Host Name")
        ;
        private final String AWS_METADATA_PATH = "http://169.254.169.254/latest/meta-data";
        private URL resourceUrl;
        private  String defaultValue;
        private AWS_METADATA_RESOURCE(String resourcePath, String defaultValue)
        {
            try
            {
                this.resourceUrl = new URL(AWS_METADATA_PATH + resourcePath);
            }
            catch (MalformedURLException error)
            {
                this.resourceUrl = null;
            }
            this.defaultValue = defaultValue;
        }
        
        String getDefaultValue()
        {
            return defaultValue;
        }
        
        URL getResourceUrl()
        {
            return resourceUrl;
        }
    }
    
    // For AWS metadata retrieval
    
    static final String INSTANCE_ID_RESOURCE = "/instance-id";
    static final String PUBLIC_HOSTNAME_RESOURCE = "/public-hostname";

    // Default values for testing in development environment
    private static final String HOST_INIT_PARAM_NAME = "serverHostURL";
    private static final String LOCAL_SYSTEM = "local";
    private static final String DEFAULT_HOST_URL = "http://localhost:8080";
    private static final String DEFAULT_CONTEXT_PATH = "";
    private static final String DEFAULT_REAL_PATH = "war";
    private static final String DEFAULT_NAME = "";
    private static final String DATABASE_SOURCES_PARAM_NAME = "databaseSources";
    private static final String DEFAULT_DATABASE_SOURCE_PARAM_NAME = "defaultDatabaseSource";
    public static final String DEFAULT_DB = "default";
    
    private static final String LOCAL_HOST_IPV4 = "127.0.0.1";
    private static final String LOCAL_HOST_NAME = "localhost";
    private static final String LOCAL_HOST_IPV6 = "0:0:0:0:0:0:0:1";
    
 // The base HTTP URL to the hosting server (e.g. http://localhost:8080/) without the context path
    private static String hostURL;
    
    // The context path to the application without the host URL
    private static String contextPath;
    
    // The file path (e.g. C:/Tomcat/webapps/myapp) to the base of the application
    private static String applicationRealPath;
    
    // The simple name of the application (e.g. myapp) (will match the directory under webapps)
    private static String applicationName;
    
    static
    {
        defaultInit();
    } // Initialize static variables to test-able defaults

    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        // Currently do nothing
    }

    /**
     * This method gets invoked when a server JVM is loaded by the Web Server container process.
     * This allows us to do early initialization of any services that we need to configure.
     */
    @Override
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        LogContext.get().setUserName(SecurityContext.SYSTEM_USER_NAME);
        LogContext.get().setRemoteIP(LOCAL_SYSTEM);

        initializeLogging();
        
        LOG.info("Starting Server at version: " + ServerVersion.getVersion());
        
        ServletContext context = contextEvent.getServletContext();
        initializeServletsContext(context);
        initializeDataLayer(context);
        initializeEmailService();
        LogContext.get().removeUserName();
        LogContext.get().removeRemoteIP();
    }
    
    public static String getInstanceMetadata(AWS_METADATA_RESOURCE metadata)
    {
        try
        {
            return IOUtils.toString(metadata.getResourceUrl().openStream());
        }
        catch (Exception error)
        {
            return "Unknown Instance ID";
        }
    }
    
    public static String getInstancePublicHostname()
    {
        return getInstanceMetadata(AWS_METADATA_RESOURCE.PUBLIC_HOSTNAME);
    }
    
    public static String getInstanceId()
    {
        if (isDebugMode())
        {
            return "local";
        }
        else
        {
            return getInstanceMetadata(AWS_METADATA_RESOURCE.INSTANCE_ID);
        }
    }
    
    public static String getApplicationURL()
    {
        return getHostURL() + getContextPath();
    }
    
    public static String getHostURL()
    {
        return hostURL;
    }
    
    public static void setHostURL(String hostURL)
    {
        ServicesManager.hostURL = hostURL;
    }
    
    public static String getContextPath()
    {
        return contextPath;
    }
    
    public static void setContextPath(String contextPath)
    {
        ServicesManager.contextPath = contextPath;
    }
    
    public static String getApplicationRealPath()
    {
        return applicationRealPath;
    }

    public static void setApplicationRealPath(String applicationPath)
    {
        ServicesManager.applicationRealPath = applicationPath;
    }
    
    public static String getApplicationName()
    {
        return applicationName;
    }

    public static void setApplicationName(String applicationName)
    {
        ServicesManager.applicationName = applicationName;
    }

    private void initializeServletsContext(ServletContext context)
    {
        String url = context.getInitParameter(HOST_INIT_PARAM_NAME);
        if ("P_HOST_URL_PARAM".equals(url)) {
            url = DEFAULT_HOST_URL;
        }
        setHostURL(url);
        LOG.info("HOST URL Path (Base URL): " + getHostURL());
        
        setContextPath(context.getContextPath());
        LOG.info("Application Context Path: " + getContextPath());
        
        setApplicationRealPath(context.getRealPath(""));
        LOG.info("Application Path: " + getApplicationRealPath());
        
        setApplicationName(context.getServletContextName());
        LOG.info("Application Name: " + getApplicationName());
    }

    private void initializeLogging()
    {
        LoggerFactory.setLoggerProvider(new Slf4jLoggerProvider());
        LOG = LoggerFactory.getLogger(ServicesManager.class);
        LOG.info("Logging service initialized");
    }

    private void initializeDataLayer(ServletContext context)
    {
        String defaultDb = context.getInitParameter(DEFAULT_DATABASE_SOURCE_PARAM_NAME);
        if (defaultDb == null || defaultDb.isEmpty() || "P_DEFAULT_DATABASE_SOURCE".equals(defaultDb)) {
            defaultDb = DEFAULT_DB;
        }
        DataService.setDefaultDatabaseName(defaultDb);
        
        Set<String> namedSources = Sets.newHashSet();
        String dbSources = context.getInitParameter(DATABASE_SOURCES_PARAM_NAME);
        if (dbSources == null || dbSources.isEmpty() || "P_DATABASE_SOURCES".equals(dbSources)) {
            namedSources.add(defaultDb);
        }
        else
        {
            namedSources = Sets.newHashSet(dbSources.split("[ ]*,[ ]*"));
        }
        
        try
        {
            LOG.debug("Starting a database session");
            // First time we open a session, the data service may do some initialization.
            // We want the initialization to happen during start-up not on the first user request.
            for (String namedSource : namedSources)
            {
            	DataService service = DataService.getDataService(namedSource);
            	service.openSession();
            }
        }
        finally
        {
            // Commit and cleanup
            LOG.debug("Committing the database session");
            for (String namedSource : DataService.getNamedSources())
            {
            	DataService.getDataService(namedSource).closeSession();
            }
        }
    }
    
    private void initializeEmailService()
    {
//        String smtpHost = EmailService.DEFAULT_SMTP_HOST;
//        String from = EmailService.DEFAULT_FROM;
//        String password = EmailService.DEFAULT_PASSWORD;
//        
//        EmailService service = new EmailService(smtpHost, from, password);
//        EmailService.setService(service);
    }

    public static void defaultInit()
    {
        setHostURL(DEFAULT_HOST_URL);
        setContextPath(DEFAULT_CONTEXT_PATH);
        setApplicationRealPath(DEFAULT_REAL_PATH);
        setApplicationName(DEFAULT_NAME);
    }
    
    public static boolean isDebugMode()
    {
        boolean isDebug = Boolean.parseBoolean(System.getProperty("jor.server.debug", "false"));
        String ip = LogContext.get().getRemoteIP();
        
        if (isDebug || ip == null || ip.contains(LOCAL_HOST_IPV4) || ip.contains(LOCAL_HOST_IPV6) ||ip.contains(LOCAL_HOST_NAME))
        {
            return true;
        }
        return false;
    }

}
