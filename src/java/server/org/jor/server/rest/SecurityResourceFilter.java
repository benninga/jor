package org.jor.server.rest;

import org.jor.server.services.security.CheckPoint;
import org.jor.server.services.security.Resources;
import org.jor.server.services.security.SecurityManager;
import org.jor.server.services.user.SecurityPolicy;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import org.jor.shared.security.Action;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.PathValue;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

public class SecurityResourceFilter implements ResourceFilter, ContainerRequestFilter
{
    private static final Logger LOG = LoggerFactory.getLogger(SecurityResourceFilter.class);
    
    private AbstractMethod method;
    private CheckPoint checkPoint;
    
    public SecurityResourceFilter(AbstractMethod am)
    {
        this.method = am;
        this.checkPoint = createCheckPoint(am);
    }
    
    @Override
    public ContainerRequestFilter getRequestFilter()
    {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter()
    {
        return null; // not needed for now
    }

    @Override
    public ContainerRequest filter(ContainerRequest request)
    {
        LOG.info("Checking security for: " + method);
        checkAccess(checkPoint);
        
        return request;
    }
    
    public static void checkAccess(CheckPoint checkPoint)
    {
        SecurityManager sm = SecurityManager.getSecurityManager();
        sm.checkAccess(checkPoint);
    }

    private static CheckPoint createCheckPoint(AbstractMethod method)
    {
        CheckPoint check = new CheckPoint();
        check.addActions(Action.REST_API_CALL);
        getAdditionalActions(check, method);
        check.addResources(getPath(method));
        return check;
    }
    
    private static void getAdditionalActions(CheckPoint check, AbstractMethod method)
    {
        addActions(check, method.getAnnotation(SecurityPolicy.class));
        addActions(check, method.getMethod().getAnnotation(SecurityPolicy.class));
        addActions(check, method.getResource().getAnnotation(SecurityPolicy.class));
    }
    
    private static void addActions(CheckPoint check, SecurityPolicy policy)
    {
        if (policy == null) {
            return;
        }
        check.addActions(policy.actions());
    }
    
    private static String getPath(AbstractMethod method)
    {
        AbstractResource resource = method.getResource();
        
        String resourcePathValue = null;
        String methodPathValue = null;
        
        PathValue resourcePath = resource.getPath();
        if (resourcePath != null) {
            resourcePathValue = resourcePath.getValue();
        }
        if (method instanceof AbstractSubResourceMethod)
        {
            PathValue subPath = ((AbstractSubResourceMethod)method).getPath();
            methodPathValue = subPath.getValue();
        }
        return constructRestPath(resourcePathValue, methodPathValue);
    }
    
    public static String constructRestPath(String resourcePath, String methodPath)
    {
        resourcePath = (resourcePath == null) ? "" : resourcePath;
        methodPath = (methodPath == null) ? "" : methodPath;
        return Resources.REST + resourcePath + methodPath;
    }
}
