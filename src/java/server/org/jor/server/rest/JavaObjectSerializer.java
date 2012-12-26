package org.jor.server.rest;

import org.jor.rest.RestConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
public class JavaObjectSerializer<T> implements MessageBodyReader<T>, MessageBodyWriter<T>
{

    public static final long UNKNOWN_SIZE = -1;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        String mediaTypeStr = mediaType.getType() + "/" + mediaType.getSubtype();
        if (RestConstants.APPLICATION_JAVA.equals(mediaTypeStr) == false) {
            return false;
        }
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                                                                                           throws IOException
    {
        return readFrom(entityStream);
    }

    public T readFrom(InputStream entityStream) throws IOException
    {
        ObjectInputStream is = new ObjectInputStream(entityStream);
        try
        {
            T obj = (T)is.readObject();
            return obj;
        }
        catch (ClassNotFoundException e)
        {
            throw new WebApplicationException(e, 406);
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
        if (RestConstants.APPLICATION_JAVA.equals(mediaTypeStr) == false) {
            return false;
        }
        return true;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                                                                                              throws IOException
    {
        writeTo(t, entityStream);
    }
    
    public void writeTo(T t, OutputStream entityStream) throws IOException
    {
        ObjectOutputStream os = new ObjectOutputStream(entityStream);
        os.writeObject(t);
    }
}
