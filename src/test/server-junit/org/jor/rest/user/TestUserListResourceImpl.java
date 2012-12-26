package org.jor.rest.user;

import org.jor.model.user.User;
import org.jor.model.user.UserInfo;
import org.jor.rest.AbstractResourceTest;
import org.jor.rest.BaseListResourceImpl;
import org.jor.server.services.db.DataService;
import org.jor.utils.CopyConstructorHelper;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestUserListResourceImpl extends AbstractResourceTest
{
    private UserListResourceImpl resource;
    
    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        resource = new UserListResourceImpl();
        updateUriInfo(BaseListResourceImpl.class, resource);
    }
    
    @Test
    public void testLoadUserHeaders()
    {
        DataService service = DataService.getDataService();
        service.save(createUser());
        service.save(createUser());
        
        List<UserInfo> headers = resource.getHeaders();
        Assert.assertEquals("Number of users", 2, headers.size());
        
    }
    
    private User createUser()
    {
        try
        {
            User user = (User)CopyConstructorHelper.constructRandom(User.class);
            return user;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
