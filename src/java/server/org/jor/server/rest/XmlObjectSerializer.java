package org.jor.server.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jor.utils.XmlUtils;

import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
public class XmlObjectSerializer<T> implements MessageBodyReader<T>, MessageBodyWriter<T>
{
    public static final long UNKNOWN_SIZE = -1;
    
    public XmlObjectSerializer()
    {
    }


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        String mediaTypeStr = mediaType.getType() + "/" + mediaType.getSubtype();
        if (MediaType.APPLICATION_XML.equals(mediaTypeStr) == false) {
            return false;
        }
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                                                                                           throws IOException,
                                                                                           WebApplicationException
    {
        int next;
        StringBuilder b = new StringBuilder();
        while ( ( next = entityStream.read()) != -1)
        {
            b.append((char)next);
        }
        String xml = b.toString();
        try
        {
            T object = XmlUtils.fromString(xml, type);
            return object;
        }
        catch (Exception e)
        {
            throw new JsonMappingException("Could not read XML string: \n" + xml, e);
        }
    }
    
    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return UNKNOWN_SIZE;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        String mediaTypeStr = mediaType.getType() + "/" + mediaType.getSubtype();
        if (MediaType.APPLICATION_XML.equals(mediaTypeStr) == false) {
            return false;
        }
        return true;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                                                                                              throws IOException,
                                                                                              WebApplicationException
    {
        String xml;
        if (t instanceof String) {
            xml = (String)t;
        } else {
            xml = XmlUtils.getString(t);
        }
        System.out.println("XML response size: " + xml.length());
        entityStream.write(xml.getBytes());
    }
}
