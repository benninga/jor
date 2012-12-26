package org.jor.shared.login;

import org.jor.shared.api.IApiInterface;

public interface IUserCredentials extends IApiInterface
{
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
}
