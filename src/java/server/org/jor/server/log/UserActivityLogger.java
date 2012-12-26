package org.jor.server.log;

import java.util.Date;

import org.jor.model.user.UserActivity;
import org.jor.server.services.ServicesManager;
import org.jor.server.services.SessionManager;
import org.jor.server.services.db.DataService;
import org.jor.server.services.user.SecurityContext;

public class UserActivityLogger
{
    public static void log(String httpMethod, String request, long duration)
    {
        // If the request was null, we've been called during a junit test, ignore.
        if (request != null)
        {
            DataService service = DataService.getDataService();
            UserActivity activity = new UserActivity();
            SecurityContext context = SessionManager.get().getSession().getSecurityContext();
            String username = context.getUsername();
            String userKeyStr = context.getUserKey();
            
            // Only log non-anon requests
            if (userKeyStr != null)
            {
                activity.setInstance(ServicesManager.getInstanceId());
                activity.setTimestamp(new Date());
                activity.setUsername(username);
                activity.setUserkey(Long.parseLong(userKeyStr));
                activity.setHttpMethod(httpMethod);
                activity.setRequest(request);
                activity.setDuration(duration);
                
                service.save(activity);
            }
        }
    }

}
