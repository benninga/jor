package org.jor.shared.utils;

import junit.framework.Assert;

import org.junit.Test;

public class TestBase64Encoder
{
    @Test
    public void testConstructor()
    {
        // For coverage enforcement
        new Base64Encoder();
    }
    
    @Test
    public void testWrongEncodedStringLength()
    {
        String bad = "abc";
        try
        {
            Base64Encoder.decode(bad);
            Assert.fail("Illegal Argument Exception should be thrown");
        }
        catch (IllegalArgumentException e)
        {
            // Expected exception
        }
    }
    
    @Test
    public void testWrongEncodedStringLetters()
    {
        String bad = "ac@#";
        try
        {
            Base64Encoder.decode(bad);
            Assert.fail("Illegal Argument Exception should be thrown");
        }
        catch (IllegalArgumentException e)
        {
            // Expected exception
        }
    }
    
    @Test
    public void testNumberConversion()
    {
        verifyEncoding("10");
        verifyEncoding("1234567890");
        verifyEncoding("0987654321");
        verifyEncoding("1111");
        verifyEncoding("2222");
        verifyEncoding("3333");
        verifyEncoding("4444");
        verifyEncoding("5555");
        verifyEncoding("6666");
        verifyEncoding("7777");
        verifyEncoding("8888");
        verifyEncoding("9999");
    }
    @Test
    public void testCharacterConversion()
    {
        verifyEncoding("abcdefghijklmnopqrstuvwxyz");
        verifyEncoding("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        verifyEncoding("aBCdEfgHiJkLmNoPqRsTuVwXyZ");
        verifyEncoding("aaaaa");
        verifyEncoding("zzzzz");
        verifyEncoding("sssss");
        verifyEncoding("yyyyy");
        verifyEncoding("hhhhh");
    }
    
    private void verifyEncoding(String value)
    {
        String encoded = Base64Encoder.encode(value);
        String decoded = Base64Encoder.decode(encoded);
        Assert.assertEquals("Same value", value, decoded);
    }
}
