package org.jor.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface BaseListResource<T> extends RestConstants
{
    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    Response getHtml();
    
    @GET
    @Consumes(APPLICATION_JAVA)
    @Produces(APPLICATION_JAVA)
    Object[] get();
    
    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    String getXML();
    
//    @GET
//    List<BaseObjectHeader> getHeaders();
}
