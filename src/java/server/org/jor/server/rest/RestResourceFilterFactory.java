package org.jor.server.rest;

import java.util.ArrayList;
import java.util.List;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class RestResourceFilterFactory implements ResourceFilterFactory
{
    @Override
    public List<ResourceFilter> create(AbstractMethod am)
    {
        List<ResourceFilter> filters = new ArrayList<>();
        filters.add(new SecurityResourceFilter(am));
        return filters;
    }
    
}
