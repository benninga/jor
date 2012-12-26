package org.jor.server.rest;

import org.jor.rest.RestConstants;
import org.jor.shared.api.rest.RestException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
public class PdfZipObjectSerializer<T> implements MessageBodyWriter<T>, RestConstants
{

    public static final long UNKNOWN_SIZE = -1;

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if (t instanceof byte[]) {
            return ((byte[])t).length;
        }
        return UNKNOWN_SIZE;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        String mediaTypeStr = mediaType.getType() + "/" + mediaType.getSubtype();
        if (APPLICATION_PDF.equals(mediaTypeStr)
                || APPLICATION_ZIP.equals(mediaTypeStr)) {
            return true;
        }
        return false;
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
        if (t instanceof byte[])
        {
            byte[] data = (byte[])t;
            entityStream.write(data);
        }
        else if (t instanceof StreamingOutput)
        {
            StreamingOutput out = (StreamingOutput)t;
            out.write(entityStream);
        }
        else if (t instanceof RestError)
        {
            RestException err = new RestException((RestError)t);
            throw new IOException(err);
        }
        else if (t instanceof Exception)
        {
            throw new IOException((Exception)t);
        }
    }
}
