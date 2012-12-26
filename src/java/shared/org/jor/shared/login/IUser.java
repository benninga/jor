package org.jor.shared.login;

import java.util.Date;

import org.jor.shared.api.IModelObject;

public interface IUser extends IModelObject
{
    int FREE_TRIAL_LENGTH = 30;
    String FIELD_USERNAME = "username";
    
    public String getSessionId();
    public void setSessionId(String sessionId);

    public Date getSessionExpiration();
    public void setSessionExpiration(Date sessionExpiration);
    
    public String getPassword();
    public void setPassword(String updatedPassword);

    public String getUsername();
    public void setUsername(String username);

    public String getEmail();
    public void setEmail(String email);
    
    public Boolean getIsAdmin();
    public void setIsAdmin(Boolean isAdmin);

    public Date getEstimatorExpiration();
    public void setEstimatorExpiration(Date estimatorExpiration);

    public Boolean getIsEulaAccepted();
    public void setIsEulaAccepted(Boolean isEulaAccepted);
    
    public Date getSiteLicenseExpiration();
    public void setSiteLicenseExpiration(Date siteLicenseExpiration);
    
    public Date getDateCreated();
    public void setDateCreated(Date dateCreated);
    
    public String getStripeCustomerToken();
    public void setStripeCustomerToken(String stripeCustomerToken);
    
    public Long getUserLibraryId();
    public void setUserLibraryId(Long libraryId);
    
    public String getCompany();
    public void setCompany(String company);

    public String getWebsite();
    public void setWebsite(String website);

    public String getPhone();
    public void setPhone(String phone);

    public Boolean getIsSubscribeToMagazine();
    public void setIsSubscribeToMagazine(Boolean isSubscribeToMagazine);
    
    public String getTags();
    public void setTags(String tags);
    
    public String getHowFoundUs();
    public void setHowFoundUs(String howFoundUs);
}
