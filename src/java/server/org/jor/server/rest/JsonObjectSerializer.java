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

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;
import org.jor.utils.JsonUtils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class JsonObjectSerializer<T> implements MessageBodyReader<T>, MessageBodyWriter<T>
{
    private final ObjectMapper objectMapper;
    private static Logger logger = LoggerFactory.getLogger(JsonObjectSerializer.class);

    public JsonObjectSerializer()
    {
        this.objectMapper = JsonUtils.createMapper();
    }

    public static final long UNKNOWN_SIZE = -1;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        String mediaTypeStr = mediaType.getType() + "/" + mediaType.getSubtype();
        if (MediaType.APPLICATION_JSON.equals(mediaTypeStr) == false) {
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
        String json = b.toString();
        try
        {
            T object = objectMapper.readValue(json, type);
            return object;
        }
        catch (JsonMappingException e)
        {
            throw new JsonMappingException("Could not read JSON string: \n" + json, e);
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
        if (MediaType.APPLICATION_JSON.equals(mediaTypeStr) == false) {
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
        String json;
        if (t instanceof String) {
            json = (String)t;
        }
        else {
            json = objectMapper.writeValueAsString(t);
        }
        logger.info("JSON response size: " + json.length());
        entityStream.write(json.getBytes());
    }
}
