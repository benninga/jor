package org.jor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class RootResourceImpl implements RootResource
{
    @GET
    @Override
    public String get()
    {
        return "Root Page - Implement me";
    }
}
