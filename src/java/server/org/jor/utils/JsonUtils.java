package org.jor.utils;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonUtils
{
    public static String getString(Object object)
    {
        try
        {
            ObjectMapper mapper = createMapper();
            String json = mapper.writeValueAsString(object);
            return json;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }
    
    public static <T> T fromString(String json, Class<T> clazz)
    {
        try
        {
            ObjectMapper mapper = createMapper();
            T result = mapper.readValue(json, clazz);
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to convert JSON to object:\n" + json, e);
        }
    }
    
    public static ObjectMapper createMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        
        // We're adding custom serializer for date and long.
        // This is due to a Long related bug in GWT 2.3
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=6331
        SimpleModule module = new SimpleModule("JOR", new Version(1, 0, 0, "", "", "")); // Any name will do.
        module.addSerializer(Date.class, new DateSerializer());
        module.addDeserializer(Date.class, new DateDeserializer());
        module.addSerializer(Long.class, new LongSerializer());
        module.addDeserializer(Long.class, new LongDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
    
    private static class DateSerializer extends JsonSerializer<Date>
    {
        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
              throws IOException, JsonProcessingException
        {
            String str = "" + value.getTime();
            jgen.writeString(str);
        }
    }
    
    private static class DateDeserializer extends JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
        {
            String str = jp.getText();
            Long value = Long.parseLong(str);
            Date date = new Date(value);
            return date;
        }
    }
    
    private static class LongSerializer extends JsonSerializer<Long>
    {
        @Override
        public void serialize(Long value, JsonGenerator jgen, SerializerProvider provider)
              throws IOException, JsonProcessingException
        {
            String str = "" + value.toString();
            jgen.writeString(str);
        }
    }
    
    private static class LongDeserializer extends JsonDeserializer<Long>
    {
        @Override
        public Long deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
        {
            String str = jp.getText();
            Long value = Long.parseLong(str);
            return value;
        }
    }
}
