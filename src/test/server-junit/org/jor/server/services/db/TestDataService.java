package org.jor.server.services.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jor.jdo.BaseJdoTestClass;
import org.jor.jdo.SampleClassJDO;
import org.jor.server.services.SessionManager;
import org.jor.server.services.db.SimplifiedQuery.Filter;
import org.jor.server.services.db.SimplifiedQuery.OP;
import org.jor.server.services.db.SimplifiedQuery.Param;
import org.jor.server.services.user.SecurityContext;

public class TestDataService extends BaseJdoTestClass
{
    private DataService service;
    
    @Override @Before
    public void setUp() throws Exception
    {
        super.setUp();
        service = DataService.getDataService();
    }
    
    @Override @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        service = null;
    }

    @Test public void testGetSession()
    {
        service.closeSession();
        service.openSession();
        try
        {
            service.openSession();
            Assert.fail("Should have had an exception for already open session");
        }
        catch (Exception e)
        {
            // This is expected
        }
    }
    
    @Test public void testSaveInvalidObject()
    {
        SampleClassJDO sample = new SampleClassJDO();
        sample.setName(randomName());
        sample.setLastUpdateAt(new Date());
        sample.setErrorString("Fail on purpose");
        
        try
        {
            service.save(sample);
            Assert.fail("Save should have failed for invalid object exception");
        }
        catch (InvalidObjectException e)
        {
            // This is expected
        }
    }
    
    @Test public void testDeleteObject()
    {
        SampleClassJDO sample = createAndSave();
        
        service.delete(sample);
        
        try
        {
            service.getObject(SampleClassJDO.class, sample.getClassKey());
            Assert.fail("We should get a not found exception");
        }
        catch (JdoObjectNotFoundException e)
        {
            // this is expected
        }
    }
    
    @Test public void testGetAllObjects_1()
    {
        String name = randomName();
        createAndSave(name);
        createAndSave();
        
        List<Filter> filters = new ArrayList<>();
        Filter filter = new Filter("name", OP.EQ, new Param("p1"));
        filters.add(filter);
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("p1", name);
        
        List<SampleClassJDO> results =
            service.getAllObjects(SampleClassJDO.class, filters, parameterValues);
        
        Assert.assertEquals("Loaded one object", 1, results.size());
    }
    
    @Test public void testRunQuery_1()
    {
        createAndSave();
        String queryStr = "from SampleClassJDO";
        List<SampleClassJDO> results = service.runQuery(queryStr);
        Assert.assertEquals("One result", 1, results.size());
    }
    
    @Test public void testRunSQLQuery_1()
    {
        createAndSave();
        
        String queryStr = "select class_id from sample_class";
        List<Object[]> result = service.runSQLQuery(queryStr);
        Assert.assertEquals("One result", 1, result.size());
    }
    
    @Test
    public void testSaveWorksWithinTransaction()
    {
        SampleClassJDO sample = createAndSave();

        List<SampleClassJDO> all = service.getAllObjects(SampleClassJDO.class);
        Assert.assertEquals("Save did not save object", 1, all.size());
        Assert.assertEquals("Same object id", sample.getId(), all.get(0).getId());
        
        // If the save did not close the transaction than the save will be reverted/rolled-back.
        service.closeSession();
        service.openSession();

        all = service.getAllObjects(SampleClassJDO.class);
        Assert.assertEquals("Save did not save object", 1, all.size());
    }
    
    @Test
    public void testDeleteWorksWithinTransaction()
    {
        SampleClassJDO sample = createAndSave();
        service.delete(sample);

        List<SampleClassJDO> all = service.getAllObjects(SampleClassJDO.class);
        Assert.assertEquals("Delete did not remove object", 0, all.size());
        
        // If the delete did not close the transaction than the delete will be reverted.
        service.closeSession();
        service.openSession();

        all = service.getAllObjects(SampleClassJDO.class);
        Assert.assertEquals("Delete did not remove object", 0, all.size());
    }
    
    @Test
    public void testCreatedByChangedOnCreateNotOnUpdate()
    {
        SampleClassJDO obj = createAndSave();
        Long creator = obj.getCreatedBy();
        

        SecurityContext sc = new SecurityContext("934527", "user2", "hello2");
        SessionManager.get().removeSession();
        SessionManager.get().createSession(sc);

        service.save(obj);
        
        Assert.assertEquals("Created by field should not change on update", creator, obj.getCreatedBy());
    }
    
    private SampleClassJDO createAndSave()
    {
        String name = randomName();
        return createAndSave(name);
    }
    
    private SampleClassJDO createAndSave(String name)
    {
        SampleClassJDO sample = new SampleClassJDO();
        sample.setName(name);
        sample.setLastUpdateAt(new Date());
        service.save(sample);
        return sample;
    }
    
    private String randomName()
    {
        return "My-Name-" + Math.random();
    }
}
