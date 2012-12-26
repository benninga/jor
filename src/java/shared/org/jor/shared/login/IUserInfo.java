package org.jor.shared.login;

import org.jor.shared.api.IApiInterface;

public interface IUserInfo extends IApiInterface
{
    Long getId();
    void setId(Long id);
    
    String getName();
    void setName(String name);
    
    String getUsername();
    void setUsername(String username);
    
    String getEmail();
    void setEmail(String email);
    
    String getCompany();
    void setCompany(String company);

    String getWebsite();
    void setWebsite(String website);

    String getPhone();
    void setPhone(String phone);

    Boolean getIsSubscribeToMagazine();
    void setIsSubscribeToMagazine(Boolean isSubscribeToMagazine);
    
    String getOldPassword();
    void setOldPassword(String oldPassword);
    
    String getNewPassword();
    void setNewPassword(String newPassword);
    
    Long getUserLibraryId();
    void setUserLibraryId(Long userLibraryId);
    
    Boolean getIsEulaAccepted();
    void setIsEulaAccepted(Boolean isEulaAccepted);
    
    String getHowFoundUs();
    void setHowFoundUs(String howFoundUs);
}
