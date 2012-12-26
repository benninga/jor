package org.jor.server.services.db;

import org.jor.server.services.user.SecurityContext;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class TestObjectKey
{
    private static final String OBJECTS_SHOULD_NOT_BE_EQUAL = "Objects should not be equal";
    private static final String OBJECTS_SHOULD_BE_EQUAL = "Objects should be equal";
    private static final String NOT_SAME_HASH_CODE = "Not same hash code";
    private static final String NULL_ID = "Null id";

    @Test public void testEmptyConstructor()
    {
        ObjectKey key = new ObjectKey();
        Assert.assertNull("ID should be null", key.getId());
        Assert.assertNull("Version should be null", key.getVersion());
    }
    
    @Test public void testConstructorWithVersion()
    {
        Long id = new Long(5);
        Long version = new Long(6);
        ObjectKey key = new ObjectKey(id, version);
        Assert.assertEquals("Id matches", id, key.getId());
        Assert.assertEquals("Id matches", version, key.getVersion());
    }
    
    @Test public void testHashCodeMethod()
    {
        Long id = new Long(50);
        ObjectKey key = new ObjectKey(id);
        Assert.assertEquals("Hashcode should match", id.hashCode(), key.hashCode());
        
        key = new ObjectKey();
        Assert.assertEquals("Hashcode should match", 1, key.hashCode());
    }
    
    @Test public void testEqualsMethod()
    {
        ObjectKey key1 = new ObjectKey(new Long(50));
        ObjectKey key2 = new ObjectKey(new Long(60));
        ObjectKey key3 = new ObjectKey(new Long(50));
        ObjectKey key4 = new ObjectKey();
        ObjectKey key5 = new ObjectKey();
        
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key1.equals(key3));
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key3.equals(key1));
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key4.equals(key5));
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key5.equals(key4));
        
        ObjectKey nullObject = null;
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key1.equals(key2));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key2.equals(key1));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key1.equals(key4));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key4.equals(key1));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key4.equals(nullObject));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key4.equals(new Date()));
    }
    
    @Test public void testEqualsMethodWithVersion()
    {
        ObjectKey key1 = new ObjectKey(new Long(6), new Long(50));
        ObjectKey key2 = new ObjectKey(new Long(6), new Long(60));
        ObjectKey key3 = new ObjectKey(new Long(6), new Long(50));
        ObjectKey key4 = new ObjectKey(new Long(6));
        ObjectKey key5 = new ObjectKey(new Long(6));
        
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key1.equals(key3));
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key3.equals(key1));
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key4.equals(key5));
        Assert.assertTrue(OBJECTS_SHOULD_BE_EQUAL, key5.equals(key4));
        

        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key1.equals(key2));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key2.equals(key1));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key1.equals(key4));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, key4.equals(key1));
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, null == key1);
        Assert.assertFalse(OBJECTS_SHOULD_NOT_BE_EQUAL, null == key4);
    }
    
    @Test public void simpleConverstion()
    {
        Long id = new Long(40);
        ObjectKey key = new ObjectKey(id);
        String convertedString = ObjectKey.convert(key);
        ObjectKey convertedKey = ObjectKey.convert(convertedString);
        Assert.assertEquals("Keys should be equal", key, convertedKey);
        String newString = ObjectKey.convert(convertedKey);
        Assert.assertEquals("Strings should be equal", convertedString, newString);
        
        ObjectKey keyWithNull = ObjectKey.convert((String)null);
        Assert.assertTrue("Key value should be null_1", keyWithNull.getId() == null);
        
        String nullString = ObjectKey.convert((ObjectKey)null);
        Assert.assertTrue("Key value should be null_2", nullString == null);
        
        nullString = ObjectKey.convert(new ObjectKey());
        Assert.assertTrue("Key value should be null_3", nullString == null);
    }
    
    @Test public void convertNullOrAnonymousKey()
    {
        ObjectKey key = ObjectKey.convert(SecurityContext.ANONYMOUS_USER_KEY);
        Assert.assertNull(NULL_ID, key.getId());
        
        key = ObjectKey.convert((String)null);
        Assert.assertNull(NULL_ID, key.getId());
    }
    
    @Test public void testConvertToString()
    {
        ObjectKey key = new ObjectKey(new Long(5));
        String value = ObjectKey.convertToString(key);
        String convertedString = ObjectKey.convertToString(value);
        Assert.assertEquals("Strings should be equal", value, convertedString);
        
        Date date = new Date();
        try
        {
            ObjectKey.convertToString(date);
            Assert.fail("An IllegalArgumentException should have been thrown");
        }
        catch (IllegalArgumentException e)
        {
            // This is expected.
        }
    }
    
    @Test public void testToString()
    {
        ObjectKey key = new ObjectKey((long)1, (long)2);
        key.toString();
    }
    
    @Test public void testNullKey()
    {
        ObjectKey key = new ObjectKey((String)null);
        Assert.assertNull(NULL_ID, key.getId());
        
        key = new ObjectKey("");
        Assert.assertNull(NULL_ID, key.getId());
        
        key = new ObjectKey((Long)null);
        Assert.assertNull(NULL_ID, key.getId());
    }
    
    @Test public void testHashCode()
    {
        ObjectKey key1 = new ObjectKey(7L, 4L);
        ObjectKey key2 = new ObjectKey(7L, 5L);
        ObjectKey key3 = new ObjectKey(5L, 4L);
        ObjectKey key4 = new ObjectKey(7L, null);
        ObjectKey key5= new ObjectKey(5L, null);
        
        Assert.assertFalse(NOT_SAME_HASH_CODE, key1.hashCode() == key2.hashCode());
        Assert.assertFalse(NOT_SAME_HASH_CODE, key1.hashCode() == key3.hashCode());
        Assert.assertFalse(NOT_SAME_HASH_CODE, key1.hashCode() == key4.hashCode());
        Assert.assertFalse(NOT_SAME_HASH_CODE, key1.hashCode() == key5.hashCode());
    }
    
}
