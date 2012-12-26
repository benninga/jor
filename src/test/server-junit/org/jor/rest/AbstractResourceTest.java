package org.jor.rest;

import org.jor.jdo.BaseJdoTestClass;

import java.lang.reflect.Field;

public class AbstractResourceTest extends BaseJdoTestClass
{
    protected void updateUriInfo(Class<?> clazz, Object resource) throws Exception
    {
        try
        {
            Field uriInfoField = clazz.getDeclaredField("uriInfo");
            uriInfoField.setAccessible(true);
            uriInfoField.set(resource, UriInfoHelper.createUriInfo());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
