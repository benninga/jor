package org.jor.rest.user;

import javax.xml.bind.annotation.XmlRootElement;

import org.jor.model.ModelBean;
import org.jor.shared.login.IUserCredentials;

@ModelBean(IUserCredentials.class)
@XmlRootElement
public class UserCredentials
{
    private String username;
    private String password;
    
    public UserCredentials()
    {
        // Required for serialization.
    }
    
    public UserCredentials(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
}
