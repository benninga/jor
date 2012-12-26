package org.jor.common;

import junit.framework.Assert;

import org.junit.Test;

public class TestPasswordUtil
{
    private static final String PASSWORD_SHOULD_MATCH = "Password should match";
    private static final String PASSWORD_SHOULD_NOT_MATCH = "Password should not match";
    @Test public void testEmptyConstructor()
    {
        new PasswordUtil();
    }
    
    @Test public void testPasswordMatch()
    {
        String password = "password";
        String password2 = "Other-Password";
        String hash = PasswordUtil.hashPassword(password);
        Assert.assertTrue(PASSWORD_SHOULD_MATCH, PasswordUtil.matchPassword(password, hash));
        Assert.assertFalse(PASSWORD_SHOULD_NOT_MATCH, PasswordUtil.matchPassword(password2, hash));
        Assert.assertFalse(PASSWORD_SHOULD_NOT_MATCH, PasswordUtil.matchPassword(password2, null));
    }
    
    @Test public void testNullPassword()
    {
        String hash1 = PasswordUtil.hashPassword("");
        String hash2 = PasswordUtil.hashPassword(null);
        Assert.assertTrue(PASSWORD_SHOULD_MATCH, PasswordUtil.matchPassword("", hash1));
        Assert.assertTrue(PASSWORD_SHOULD_MATCH, PasswordUtil.matchPassword("", hash2));
        Assert.assertTrue(PASSWORD_SHOULD_MATCH, PasswordUtil.matchPassword(null, hash1));
        Assert.assertTrue(PASSWORD_SHOULD_MATCH, PasswordUtil.matchPassword(null, hash2));
    }
    
    @Test public void testRemovePasswordPrefix()
    {
        String unPrefixed = "Unprefixed-Text";
        String prefixed = PasswordUtil.addPasswordPrefix(unPrefixed);
        String converted = PasswordUtil.removePasswordPrefix(prefixed);
        Assert.assertEquals("Matching Password", unPrefixed, converted);
        
        try
        {
            PasswordUtil.removePasswordPrefix(unPrefixed);
            Assert.fail("Should have failed with exception due to missing prefix");
        }
        catch (IllegalArgumentException e)
        {
            // This is expected.
        }
    }
}

