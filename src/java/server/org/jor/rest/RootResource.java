package org.jor.rest;

import javax.ws.rs.GET;

public interface RootResource
{
    @GET
    String get();
}
