package org.jor.rest.user;

import static org.jor.shared.api.rest.ProjectResourceConstants.HEADERS_PATH;
import static org.jor.shared.api.rest.UserResourceConstants.USER_PATH;

import org.jor.model.user.User;
import org.jor.model.user.UserInfo;
import org.jor.rest.BaseListResourceImpl;
import org.jor.rest.RestConstants;
import org.jor.server.services.db.DataHelper;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Path(USER_PATH)
public class UserListResourceImpl extends BaseListResourceImpl<User> implements RestConstants
{
    @Override
    protected String getResourceTemplateFile()
    {
        return USER_LIST_PAGE_FTL;
    }

    @Override
    protected Class<User> getResourceType()
    {
        return User.class;
    }

    @Override
    protected User copyConstructor(User object)
    {
        return new User(object);
    }
    
    @GET
    @Path(HEADERS_PATH)
    @Produces({APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<UserInfo> getHeaders()
    {
        MultivaluedMap<String, String> queryParams = getUriInfo().getQueryParameters();
        List<User> users = DataHelper.getAllObjects(User.class, queryParams);
        List<UserInfo> headers = new ArrayList<>(users.size());
        for (User user : users)
        {
            UserInfo info = new UserInfo(user);
            headers.add(info);
        }
        return headers;
    }
}
