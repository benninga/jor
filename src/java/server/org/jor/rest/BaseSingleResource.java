package org.jor.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public interface BaseSingleResource<T> extends RestConstants
{
    @GET
    @Produces(MediaType.TEXT_HTML)
    Response getHtml();
    
    @GET
    @Produces(APPLICATION_JAVA)
    T get();
    
    @GET
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    T getXML();
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    T getJSON();
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    Response update(MultivaluedMap<String, String> form);
    
    @POST
    @Consumes(APPLICATION_JAVA)
    @Produces({ APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    T update(T object);
}
