package org.jor.model.user;

import java.io.Serializable;

import org.jor.model.ModelBean;
import org.jor.shared.login.IUserInfo;

@ModelBean(IUserInfo.class)
public class UserInfo implements IUserInfo, Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String FIELD_ID = "id";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_OLD_PASSWORD = "oldPassword";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_NEW_PASSWORD = "newPassword";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_USER_LIBRARY_ID = "userLibraryId";
    public static final String FIELD_COMPANY = "company";
    public static final String FIELD_WEBSITE = "website";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_IS_SUBSCRIBE_TO_MAGAZINE = "isSubscribeToMagazine";
    public static final String FIELD_IS_EULA_ACCEPTED = "isEulaAccepted";
    public static final String FIELD_HOW_FOUND_US = "howFoundUs";
    public static final String FIELD_PREFERRED_INSULATION_UNIT = "preferredInsulationUnit";
    
    private Long id;
    private String name;
    private String username;
    private String email;
    
    private String company;
    private String website;
    private String phone;
    private Boolean isSubscribeToMagazine;
    private String howFoundUs;
    
    private String oldPassword;
    private String newPassword;
    private Long userLibraryId;
    
    private Boolean isEulaAccepted;
    
    public UserInfo()
    {
        //Serialization
        isEulaAccepted = Boolean.FALSE;
    }
    
    public UserInfo(String name, String username, String email,
                    String phone, String company, String website,
                    String oldPassword, String newPassword,
                    Long userLibraryId, boolean isEulaAccepted, String howFoundUs)
    {
        setName(name);
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setCompany(company);
        setWebsite(website);
        setOldPassword(oldPassword);
        setNewPassword(newPassword);
        setUserLibraryId(userLibraryId);
        setIsEulaAccepted(isEulaAccepted);
        setHowFoundUs(howFoundUs);
    }
    
    public UserInfo(UserInfo other)
    {
        setId(other.getId());
        setName(other.getName());
        setUsername(other.getUsername());
        setEmail(other.getEmail());
        setOldPassword(other.getOldPassword());
        setNewPassword(other.getNewPassword());
        setUserLibraryId(other.getUserLibraryId());
        
        setCompany(other.getCompany());
        setWebsite(other.getWebsite());
        setPhone(other.getPhone());
        setIsSubscribeToMagazine(other.getIsSubscribeToMagazine());
        setIsEulaAccepted(other.getIsEulaAccepted());
        setHowFoundUs(other.getHowFoundUs());
    }
    
    public UserInfo(User other)
    {
        setId(other.getId());
        setName(other.getName());
        setUsername(other.getUsername());
        setEmail(other.getEmail());
        setUserLibraryId(other.getUserLibraryId());
        
        setCompany(other.getCompany());
        setWebsite(other.getWebsite());
        setPhone(other.getPhone());
        setIsSubscribeToMagazine(other.getIsSubscribeToMagazine());
        setIsEulaAccepted(other.getIsEulaAccepted());
        setHowFoundUs(other.getHowFoundUs());
    }
    
    @Override
    public Long getId()
    {
        return id;
    }
    
    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public void setUsername(String username)
    {
        if (username.length() < 2)
        {
            throw new IllegalArgumentException("Username must be 2 or more characters");
        }
        
        this.username = username;
    }

    @Override
    public String getEmail()
    {
        return email;
    }

    @Override
    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public String getOldPassword()
    {
        return oldPassword;
    }

    @Override
    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

    @Override
    public String getNewPassword()
    {
        return newPassword;
    }

    @Override
    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    @Override
    public Long getUserLibraryId()
    {
        return userLibraryId;
    }

    @Override
    public void setUserLibraryId(Long userLibraryId)
    {
        this.userLibraryId = userLibraryId;
    }

    @Override
    public String getCompany()
    {
        return company;
    }

    @Override
    public void setCompany(String company)
    {
        this.company = company;
    }

    @Override
    public String getWebsite()
    {
        return website;
    }

    @Override
    public void setWebsite(String website)
    {
        this.website = website;
    }

    @Override
    public String getPhone()
    {
        return phone;
    }

    @Override
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    @Override
    public Boolean getIsSubscribeToMagazine()
    {
        return isSubscribeToMagazine;
    }

    @Override
    public void setIsSubscribeToMagazine(Boolean isSubscribeToMagazine)
    {
        this.isSubscribeToMagazine = isSubscribeToMagazine;
    }
    
    @Override
    public Boolean getIsEulaAccepted()
    {
        return isEulaAccepted;
    }
    
    @Override
    public void setIsEulaAccepted(Boolean isEulaAccepted)
    {
        this.isEulaAccepted = isEulaAccepted;
        if (isEulaAccepted == null) {
            isEulaAccepted = Boolean.FALSE;
        }
    }
    
    @Override
    public String getHowFoundUs()
    {
        return howFoundUs;
    }
    
    @Override
    public void setHowFoundUs(String howFoundUs)
    {
        this.howFoundUs = howFoundUs;
        if (this.howFoundUs == null) {
            this.howFoundUs = "";
        }
    }
}
