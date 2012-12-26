package org.jor.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlUtils
{
    public static String getString(Object object)
    {
        return getString(object, object.getClass());
    }
    
    public static String getString(Object object, Class<?> ... classes )
    {
        JAXBContext context;
        context = getContext(classes);
        return getString(object, context);
    }
    
    public static String getString(Object object, JAXBContext context)
    {
        try
        {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(object, writer);
            String xml = writer.toString();
            return xml;
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T fromString(String xmlStr, Class<?> ... classes)
    {
        try
        {
            JAXBContext context = getContext(classes);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T object = (T)unmarshaller.unmarshal(new StringReader(xmlStr));
            return object;
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    protected static JAXBContext getContext(Class<?> ... classes)
    {
        try
        {
            return JAXBContext.newInstance(classes);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
        
    }
}
