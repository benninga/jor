package org.jor.server.rest;

import org.jor.jdo.SampleClassJDO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;


public class TestJavaObjectSerializer
{
    private static final String APPLICATION = "application";
    @Test public void testObjectLength()
    {
        JavaObjectSerializer<String> work = new JavaObjectSerializer<>();
        long size = work.getSize(null, null, null, null, null);
        Assert.assertEquals("Unknown Size", JavaObjectSerializer.UNKNOWN_SIZE, size);
    }
    
    @Test public void testObjectReadable()
    {
        JavaObjectSerializer<SampleClassJDO> work = new JavaObjectSerializer<>();
        MediaType mediaType = new MediaType(APPLICATION, "java");
        boolean value = work.isReadable(SampleClassJDO.class, SampleClassJDO.class, null, mediaType);
        Assert.assertEquals("Should be readable", true, value);
        
        //Test False 
        
        JavaObjectSerializer<SampleClassJDO> work2 = new JavaObjectSerializer<>();
        MediaType mediaType2 = new MediaType(APPLICATION, "java2");
        boolean value2 = work2.isReadable(SampleClassJDO.class, SampleClassJDO.class, null, mediaType2);
        Assert.assertEquals("Should be readable", false, value2);
    }
    
    @Test public void testObjectWriteable()
    {
        JavaObjectSerializer<SampleClassJDO> work = new JavaObjectSerializer<>();
        MediaType mediaType = new MediaType(APPLICATION, "java");
        boolean value = work.isWriteable(SampleClassJDO.class, SampleClassJDO.class, null, mediaType);
        Assert.assertEquals("Should be writeable", true, value);
    }
    
    @Test public void testObjectSerialization() throws IOException
    {
        JavaObjectSerializer<SampleClassJDO> work = new JavaObjectSerializer<>();
        SampleClassJDO object = new SampleClassJDO();
        object.setName("First-" + Math.random());
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        work.writeTo(object, SampleClassJDO.class, SampleClassJDO.class, null, null, null, os);
        byte[] bytes = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        SampleClassJDO read = work.readFrom(SampleClassJDO.class, SampleClassJDO.class, null, null, null, is);
        Assert.assertEquals("Size should indicate unknown size", object.getName(), read.getName());
        Assert.assertNotSame("Not same object instance", object, read);
    }
}
