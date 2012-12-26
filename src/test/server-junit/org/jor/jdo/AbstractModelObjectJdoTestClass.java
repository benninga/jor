package org.jor.jdo;

import junit.framework.Assert;

import org.junit.Test;

import org.jor.server.services.db.DataService;
import org.jor.server.services.db.ObjectJDO;
import org.jor.utils.CopyConstructorHelper;
import org.jor.utils.JsonUtils;

public abstract class AbstractModelObjectJdoTestClass<T extends ObjectJDO> extends BaseJdoTestClass
{
    protected abstract T createDefaultObject();
    protected abstract T copyConstructObject(T original);
    
    @Test
    public void testComponentsRemainAssociatedToLibraryAfterCopy()
    {
        DataService service = DataService.getDataService();

        // Setup the library
        T object = createDefaultObject();
        
        // Save the library
        service.begin();
        object = service.save(object);
        object = copyConstructObject(object);
        String json = JsonUtils.getString(object);
        service.commit();

        // Close and open the session to imitate new HTTP request
        service.closeSession();
        service.openSession();
        
        T serialized = (T)JsonUtils.fromString(json, object.getClass());
        service.save(serialized);
        
        // Close and open the session to imitate new HTTP request
        service.closeSession();
        service.openSession();

        T loaded = (T)service.getObject(object.getClass(), object.getKey());
        
        compareObjects(serialized, loaded);
    }
    
    protected void compareObjects(T a, T b)
    {
        String result = CopyConstructorHelper.compareObjects(a, b, true);
        Assert.assertNull("Loaded object not the same as saved object: \n" + result, result);   
    }
}
