package org.jor.server.services.db;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jor.server.log.LogContext;
import org.jor.server.services.user.SecurityContext;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import com.google.common.collect.Sets;

public class DataLayerThreadFilter implements Filter
{

    private static Logger logger = LoggerFactory.getLogger(DataLayerThreadFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        Set<DataService> services = Sets.newHashSet();
        try
        {
            LogContext.get().setUserName(SecurityContext.SYSTEM_USER_NAME);
            LogContext.get().setRemoteIP(request.getRemoteAddr());
            
            logger.debug("Starting a database session");
            for (String namedSource : DataService.getNamedSources())
            {
                DataService service = DataService.getDataService(namedSource);
                service.openSession();
                services.add(service);
            }

            // Call the next filter (continue request processing)
            chain.doFilter(request, response);

        }
        finally
        {
            // Commit and cleanup
            logger.debug("Committing the database session");
            for (DataService service : services)
            {
                try {
                    service.closeSession();
                } catch (Exception e) {
                    logger.error("Failed to close database session", e);
                }
            }
            
            LogContext.get().removeUserName();
            LogContext.get().removeRemoteIP();
        }
    }

    @Override
    public void init(FilterConfig filterConfig)
    {
        logger.debug("DataLayerThreadFilter, Initializing filter...");
        // For now, do nothing.
    }

    @Override public void destroy()
    {
        logger.debug("DataLayerThreadFilter, Destroying filter...");
        // Do nothing for now.
    }
}
