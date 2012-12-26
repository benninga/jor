package org.jor.server.services.security;

import static org.jor.server.services.user.SecurityContext.ANONYMOUS_USER_KEY;
import static org.jor.shared.security.PermissionsManager.securityDisabled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jor.model.user.User;
import org.jor.server.services.Session;
import org.jor.server.services.SessionManager;
import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectKey;
import org.jor.server.services.user.SecurityContext;
import org.jor.shared.security.Action;
import com.google.common.collect.Lists;

public class SecurityManager implements Resources
{
    private static final String DATE_FORMAT_PATTERN = "MM-dd-yyyy";
    private static final String USER_MUST_BE_LOGGED_IN = "User must be logged in";

    private static final List<String> UNSECURED_RESOURCES = Lists.newArrayList(
            REST_LOGIN_RESOURCE,
            REST_LOGOUT_RESOURCE,
            REST_NEW_USER_RESOURCE
            );

    private static final SecurityManager singleton = new SecurityManager();

    public static SecurityManager getSecurityManager()
    {
        return singleton;
    }

    private SecurityManager()
    {
        // Make sure constructor is not called except through this class.
    }

    public AccessCheckResult hasAccess(Action action)
    {
        CheckPoint check = new CheckPoint().addActions(action);
        return hasAccess(check);
    }

    public AccessCheckResult hasAccess(CheckPoint checkPoint)
    {
        AccessCheckResult result = new AccessCheckResult();

        User user = getCurrentUser();
        if (user != null && user.getIsAdmin()) {
            return result.allow();
        }

        for (Action action : checkPoint.getActions())
        {
            if (Action.REST_API_CALL == action)
            {
                hasRestAccess(checkPoint, user, result);
            }
            else if (Action.CREATE == action || Action.UPDATE == action || Action.DELETE == action)
            {
                hasCreateModelObjectAccess(user, result);
            }
            else if (Action.RESET_PASSWORD == action)
            {
                hasResetPasswordAccess(user, result);
            }
            else
            {
                verifyUserLoggedIn(user, result);
            }
        }

        return result;
    }

    public void checkAccess(CheckPoint checkPoint)
    {
        AccessCheckResult result = hasAccess(checkPoint);
        if (result.getHasAccess() == false)
        {
//            throw new NoAccessException(result);
        }
    }

    private void hasResetPasswordAccess(User user, AccessCheckResult result)
    {
        verifyUserLoggedIn(user, result);

        if (user == null) {
            return;
        }

        if (user.getIsAdmin()) {
            result.allow();
        }
        else {
            result.deny();
        }
    }

    private void hasCreateModelObjectAccess(User user, AccessCheckResult result)
    {
        verifyUserLoggedIn(user, result);

        if (user == null) {
            return;
        }

        Date siteLicense = (user.getSiteLicenseExpiration() == null) ? new Date(0) : user.getSiteLicenseExpiration();
        Calendar maintenanceExpiration = Calendar.getInstance();
        maintenanceExpiration.setTime(siteLicense);

        Calendar now = Calendar.getInstance();

        if (maintenanceExpiration.after(now) || securityDisabled)
        {
            result.allow();
        }
        else
        {
            result.deny();
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            String expiration = formatter.format(siteLicense);
            result.setDenyReason("License expired on " + expiration + ". An active license is required to perform this action.");
        }
    }

    private void hasRestAccess(CheckPoint checkPoint,
                               User user, AccessCheckResult result)
    {
        for (String resource : checkPoint.getResources())
        {
            if (UNSECURED_RESOURCES.contains(resource))
            {
                result.allow();
            }
            else if (REST_EULA_RESOURCE.equals(resource))
            {
                verifyUserLoggedIn(user, result);
            }
            else
            {
                verifyUserLoggedInAndEulaSigned(user, result);
            }
        }
    }

    private void verifyUserLoggedInAndEulaSigned(User user, AccessCheckResult result)
    {
        if (!verifyUserLoggedIn(user, result))
        {
            return;
        }
    }

    private boolean verifyUserLoggedIn(User user, AccessCheckResult result)
    {
        if (user == null)
        {
            result.deny();
            result.setDenyReason(USER_MUST_BE_LOGGED_IN);
            return false;
        }
        else
        {
            result.allow();
            return true;
        }
    }

    public User getCurrentUser()
    {
        Session session = SessionManager.get().getSession();
        SecurityContext sc = session.getSecurityContext();
        String keyStr = sc.getUserKey();
        if (keyStr == null
                || ANONYMOUS_USER_KEY != null && ANONYMOUS_USER_KEY.equals(keyStr))
        {
            return null;
        }
        ObjectKey key = ObjectKey.convert(keyStr);
        User user = DataService.getDataService().getObject(User.class, key);
        return user;
    }

}
