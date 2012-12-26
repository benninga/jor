package org.jor.rest.user;

import org.junit.Assert;
import org.junit.Test;

import org.jor.utils.XmlUtils;

public class TestUserCredentials
{
    @Test
    public void convertToXML()
    {
        UserCredentials credentials = new UserCredentials("user", "password");
        String xml = XmlUtils.getString(credentials);
        UserCredentials converted = XmlUtils.fromString(xml, UserCredentials.class);
        Assert.assertEquals("Same username", credentials.getUsername(), converted.getUsername());
    }
}
