package org.jor.server.rest;

import org.jor.utils.HtmlPrintable;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
public class HtmlObjectSerializer<T> implements MessageBodyWriter<T>
{

    public static final long UNKNOWN_SIZE = -1;

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return UNKNOWN_SIZE;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        String mediaTypeStr = mediaType.getType() + "/" + mediaType.getSubtype();
        if (MediaType.TEXT_HTML.equals(mediaTypeStr)
                || MediaType.TEXT_PLAIN.equals(mediaTypeStr)) {
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
        String value;
        if (t instanceof String)
        {
            value = (String)t; 
        }
        else if (t instanceof HtmlPrintable)
        {
            value = ((HtmlPrintable)t).toHtml();
        }
        else
        {
            StringBuilder message = new StringBuilder();
            message.append("<html>\n");
            message.append("<body>\n");
            String object = t.toString().replaceAll("\n", "<br/>");
            message.append(object).append("\n");
            message.append("</body>\n");
            message.append("</html>\n");
            value = message.toString();
        }
        entityStream.write(value.getBytes());
    }
}
