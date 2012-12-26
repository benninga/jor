package org.jor.rest.user;

import static org.jor.rest.RestConstants.ID;
import static org.jor.shared.api.rest.UserResourceConstants.INFO_PATH;
import static org.jor.shared.api.rest.UserResourceConstants.SINGLE_USER_PATH;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.jor.model.user.User;
import org.jor.model.user.UserInfo;
import org.jor.rest.BaseListResource;
import org.jor.rest.BaseSingleResourceImpl;
import org.jor.rest.RestConstants;
import org.jor.server.services.db.DataHelper;

@Path(SINGLE_USER_PATH + "/{" + ID + "}" )
public class UserResourceImpl extends BaseSingleResourceImpl<User> implements RestConstants
{

    @Override
    protected User getNewObject()
    {
        return new User();
    }

    @Override
    protected User getExistingObject(Long id)
    {
        return new User(DataHelper.getObject(User.class, id));
    }
    
    @Override
    protected User createOrUpdate(User user)
    {
        return super.createOrUpdate(user);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public User create(JAXBElement<User> jaxbObject)
    {
        return super.baseXMLCreate(jaxbObject.getValue());
    }
    
    @POST
    @Path(INFO_PATH)
    @Produces({ APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateWithInfo(UserInfo info)
    {
        User user = get();
        user.updateFromInfo(info);
        update(user);
    }
    
    @GET
    @Path(INFO_PATH)
    @Produces({ APPLICATION_JAVA, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public UserInfo getUserInfo()
    {
        User user = get();
        return new UserInfo(user);
    }
    
    @Override
    protected Class<? extends BaseListResource<User>> getListResource()
    {
        return UserListResourceImpl.class;
    }

    @Override
    protected String getResourceTemplateFile()
    {
        return USER_PAGE_FTL;
    }

    @Override
    protected User copyConstructor(User item)
    {
        return new User(item);
    }
}
