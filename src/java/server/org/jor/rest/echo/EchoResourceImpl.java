package org.jor.rest.echo;

import static org.jor.shared.api.rest.EchoResourceConstants.ECHO_PATH;
import static org.jor.shared.api.rest.EchoResourceConstants.JSON_PATH;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jor.rest.RestConstants;

@Path(ECHO_PATH )
public class EchoResourceImpl
{
    @GET
    @Produces({ MediaType.APPLICATION_JSON, RestConstants.APPLICATION_JAVA, MediaType.TEXT_HTML })
    public String get(@QueryParam("echo") String echo)
    {
        return echo;
    }
    
    @GET
    @Path(JSON_PATH)
    public String getJSON(@QueryParam("echo") String echo)
    {
        return "{ \"echo\" : \"" + echo + "\" }";
    }
    
}
