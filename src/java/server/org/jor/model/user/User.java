package org.jor.model.user;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlTransient;

import org.jor.common.PasswordUtil;
import org.jor.jdo.ModelObject;
import org.jor.model.ModelBean;
import org.jor.server.services.security.SecurityManager;
import org.jor.shared.login.IUser;
import org.jor.shared.security.Action;

@ModelBean(value=IUser.class)
public class User extends ModelObject implements IUser
{
    private static final long serialVersionUID = 1L;

    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_SESSION_EXPIRATION = "sessionExpiration";
    public static final String FIELD_SESSION_ID = "sessionId";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_IS_ADMIN = "isAdmin";
    public static final String FIELD_ESTIMATOR_EXPIRATION = "estimatorExpiration";
    public static final String FIELD_IS_EULA_ACCEPTED = "isEulaAccepted";
    public static final String FIELD_SITE_LICENSE_EXPIRATION = "siteLicenseExpiration";
    public static final String FIELD_DATE_CREATED = "dateCreated";
    public static final String FIELD_STRIPE_CUSTOMER_TOKEN = "stripeCustomerToken";
    public static final String FIELD_USER_LIBRARY_ID = "userLibraryId";
    public static final String FIELD_COMPANY = "company";
    public static final String FIELD_WEBSITE = "website";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_IS_SUBSCRIBE_TO_MAGAZINE = "isSubscribeToMagazine";
    public static final String FIELD_TAGS = "tags";
    public static final String FIELD_HOW_FOUND_US = "howFoundUs";
    public static final String FIELD_PREFERRED_INSULATION_UNIT = "preferredInsulationUnit";
    
    private static Integer ADMIN_VALUE = 1;
    private static Integer NOT_ADMIN_VALUE = 0;
    
    private String password;
    private String username;
    private String email;
    private String company;
    private String website;
    private String phone;
    private Boolean isSubscribeToMagazine;
    private String tags;
    private String howFoundUs;
    
    private String sessionId;
    private Date sessionExpiration;
    private boolean isAdmin;
    private Date estimatorExpiration;
    private Boolean isEulaAccepted;
    private Date siteLicenseExpiration;
    private Date dateCreated;
    private String stripeCustomerToken;
    private Long userLibraryId;
    
    public User()
    {
        sessionExpiration = null;
        isAdmin = false;
        this.dateCreated = new Date();
        
        // Update site license to be expired
        Calendar siteDate = Calendar.getInstance();
        siteDate.add(Calendar.DAY_OF_MONTH, -1); // expired
        siteLicenseExpiration = siteDate.getTime();
        
        // We offer free period for new users
        Calendar estimatorDate = Calendar.getInstance();
        estimatorDate.add(Calendar.DAY_OF_MONTH, FREE_TRIAL_LENGTH);
        estimatorExpiration = estimatorDate.getTime();
        
        isEulaAccepted = false;
        howFoundUs = "";
    }
    
    public User(User other)
    {
        this(other, true);
    }
    
    public User(User other, boolean copyId)
    {
        super(other, copyId);
        this.password = other.getPassword();
        setUsername(other.getUsername());
        setEmail(other.getEmail());
        setSessionId(other.getSessionId());
        setSessionExpiration(other.getSessionExpiration());
        setIsAdmin(other.getIsAdmin());
        setEstimatorExpiration(new Date(other.getEstimatorExpiration().getTime()));
        setIsEulaAccepted(other.getIsEulaAccepted());
        setSiteLicenseExpiration(other.getSiteLicenseExpiration());
        setDateCreated(other.getDateCreated());
        setStripeCustomerToken(other.getStripeCustomerToken());
        setUserLibraryId(other.getUserLibraryId());
        
        setCompany(other.getCompany());
        setWebsite(other.getWebsite());
        setPhone(other.getPhone());
        setIsSubscribeToMagazine(other.getIsSubscribeToMagazine());
        setTags(other.getTags());
        setHowFoundUs(other.getHowFoundUs());
    }
    
    
    @Override
    public Date getSiteLicenseExpiration()
    {
        return siteLicenseExpiration;
    }

    
    @Override
    public void setSiteLicenseExpiration(Date siteLicenseExpiration)
    {
        this.siteLicenseExpiration = siteLicenseExpiration;
    }

    
    @Override
    public String getSessionId()
    {
        return sessionId;
    }

    
    @Override
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    
    @Override
    public Date getSessionExpiration()
    {
        return sessionExpiration;
    }

    
    @Override
    public void setSessionExpiration(Date sessionExpiration)
    {
        this.sessionExpiration = sessionExpiration;
    }
    
    
    @Override
    @XmlTransient
    public String getPassword() {
        return password;
    }
    
    
    @Override
    public void setPassword(String updatedPassword)
    {
        if(updatedPassword != null && updatedPassword.startsWith(PasswordUtil.HASH_PREFIX)) {
            this.password = updatedPassword;
        } else {
            this.password = PasswordUtil.hashPassword(updatedPassword);
        }
    }

    
    @Override
    public String getUsername()
    {
        return username;
    }

    
    @Override
    public void setUsername(String username)
    {
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
    public Boolean getIsAdmin()
    {
        return isAdmin;
    }

    
    @Override
    public void setIsAdmin(Boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }

    
    @Override
    public Date getEstimatorExpiration()
    {
        return estimatorExpiration;
    }

    
    @Override
    public void setEstimatorExpiration(Date maintenanceExpiration)
    {
        this.estimatorExpiration = maintenanceExpiration;
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
    public String verifyObject()
    {
        return null;
    }

    
    @Override
    public Map<String, Object> entityToMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, getId());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_USERNAME, getUsername());
        map.put(FIELD_EMAIL, getEmail());
        map.put(FIELD_SESSION_ID, getSessionId());
        map.put(FIELD_SESSION_EXPIRATION, getSessionExpiration());
        map.put(FIELD_LAST_UPDATE_AT, toDateTimeString(getLastUpdateAt()));
        map.put(FIELD_IS_ADMIN, getIsAdmin() ? ADMIN_VALUE : NOT_ADMIN_VALUE);
        map.put(FIELD_ESTIMATOR_EXPIRATION, toDateString(getEstimatorExpiration()));
        map.put(FIELD_IS_EULA_ACCEPTED, getIsEulaAccepted());
        map.put(FIELD_SITE_LICENSE_EXPIRATION, toDateString(getSiteLicenseExpiration()));
        map.put(FIELD_DATE_CREATED, toDateString(getDateCreated()));
        map.put(FIELD_USER_LIBRARY_ID, getUserLibraryId());
        
        map.put(FIELD_COMPANY, getCompany());
        map.put(FIELD_WEBSITE, getWebsite());
        map.put(FIELD_PHONE, getPhone());
        map.put(FIELD_TAGS, getTags());
        map.put(FIELD_IS_SUBSCRIBE_TO_MAGAZINE, toString(getIsSubscribeToMagazine()));
        map.put(FIELD_HOW_FOUND_US, getHowFoundUs());
        return map;
    }
    
    
    @Override
    public void objectFromMap(MultivaluedMap<String, String> form)
    {
        super.objectFromMap(form);
        setId(getLong(form.getFirst(FIELD_ID)));
        setName(form.getFirst(FIELD_NAME));
        setUsername(form.getFirst(FIELD_USERNAME));
        setEmail(form.getFirst(FIELD_EMAIL));
        setLastUpdateAt(new Date());
        setIsAdmin(getInt(form.getFirst(FIELD_IS_ADMIN)) == ADMIN_VALUE);
        setEstimatorExpiration(getDate(form.getFirst(FIELD_ESTIMATOR_EXPIRATION)));
        setIsEulaAccepted(Boolean.parseBoolean(form.getFirst(FIELD_IS_EULA_ACCEPTED)));
        setSiteLicenseExpiration(getDate(form.getFirst(FIELD_SITE_LICENSE_EXPIRATION)));
        setDateCreated(getDate(form.getFirst(FIELD_DATE_CREATED)));
        setUserLibraryId(getLong(form.getFirst(FIELD_USER_LIBRARY_ID)));
        
        setCompany(form.getFirst(FIELD_COMPANY));
        setWebsite(form.getFirst(FIELD_WEBSITE));
        setPhone(form.getFirst(FIELD_PHONE));
        setTags(form.getFirst(FIELD_TAGS));
        setIsSubscribeToMagazine(Boolean.parseBoolean(form.getFirst(FIELD_IS_SUBSCRIBE_TO_MAGAZINE)));
        setHowFoundUs(form.getFirst(FIELD_HOW_FOUND_US));
        
        String oldPassword = form.getFirst("oldPassword");
        String pwd = form.getFirst(FIELD_PASSWORD);
        String verifyPassword = form.getFirst("verifyPassword");
        updatePasswordIfNeeded(oldPassword, pwd, verifyPassword);
    }
    
    public void updateFromInfo(UserInfo info)
    {
        setName(info.getName());
        setUsername(info.getUsername());
        setEmail(info.getEmail());
        
        setCompany(info.getCompany());
        setWebsite(info.getWebsite());
        setPhone(info.getPhone());
        setIsSubscribeToMagazine(info.getIsSubscribeToMagazine());
        setIsEulaAccepted(info.getIsEulaAccepted());
        setHowFoundUs(info.getHowFoundUs());
        
        String oldPassword = info.getOldPassword();
        String newPassword = info.getNewPassword();
        // Verification that new password is correctly typed by user has already happened on the client.
        updatePasswordIfNeeded(oldPassword, newPassword, newPassword);
    }
    
    private void updatePasswordIfNeeded(String oldPassword,
                                        String pwd, String verifyPassword)
    {
        if (pwd == null || pwd.length() == 0) {
            return;
        }
        
        String currentPwd = getPassword();
        if (currentPwd != null && currentPwd.length() > 0) // new object
        {
            // Make sure old password verifies for new password
            boolean match = PasswordUtil.matchPassword(oldPassword, getPassword());
            SecurityManager sm = SecurityManager.getSecurityManager();
            if (match == false && ! sm.hasAccess(Action.RESET_PASSWORD).getHasAccess())
            {
                throw new RuntimeException("Old password does not match");
            }
        }
        
        if (pwd.equals(verifyPassword) == false)
        {
            throw new RuntimeException("Password does not match verification");
        }
        else
        {
            setPassword(pwd);
        }
    }

    
    @Override
    public Date getDateCreated()
    {
        return dateCreated;
    }

    
    @Override
    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    
    @Override
    public String getStripeCustomerToken()
    {
        return stripeCustomerToken;
    }

    
    @Override
    public void setStripeCustomerToken(String stripeCustomerToken)
    {
        this.stripeCustomerToken = stripeCustomerToken;
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
    public String getTags()
    {
        return tags;
    }

    @Override
    public void setTags(String tags)
    {
        this.tags = tags;
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
