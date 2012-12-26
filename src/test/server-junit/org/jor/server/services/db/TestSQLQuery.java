package org.jor.server.services.db;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.QueryException;
import org.junit.Test;

import org.jor.jdo.SampleClassJDO;

public class TestSQLQuery extends AbstractQueryTest
{
    private static final String NO_VALUES_RETURNED = "No values returned";
    private static final String VALUES_RETURNED = "Values returned";
    private static final String SELECT_CLASS_ID_FROM_SAMPLE_CLASS_WHERE_CLASS_ID_P1 =
        "select class_id from sample_class WHERE class_id = :p1";
    private static final String SELECT_CLASS_ID_FROM_SAMPLE_CLASS =
        "select class_id from sample_class";
    private static final String P1 = "p1";

    @Test
    public void testColumnNames()
    {
        String queryStr = SELECT_CLASS_ID_FROM_SAMPLE_CLASS;
        SQLQuery query = new SQLQuery(getSession(), queryStr);
        testColumnNames(queryStr, query);
        
        query = new SQLQuery(getSession(), queryStr, new String[] { "class_id" } );
        testColumnNames(queryStr, query);
        
        query = new SQLQuery(getSession(), queryStr, new String[0]);
        testColumnNames(queryStr, query);
    }
    
    private void testColumnNames(String queryStr, SQLQuery query)
    {
        Assert.assertEquals("Same query string", queryStr, query.getQueryString());
        
        // Verify no exception happens when column names are null
        query.createQuery(queryStr);
    }
    
    @Test
    public void testExecuteQuery_1()
    {
        String queryStr = SELECT_CLASS_ID_FROM_SAMPLE_CLASS;
        SQLQuery query = new SQLQuery(getSession(), queryStr);
        List<Integer> values = query.execute();
        Assert.assertEquals(NO_VALUES_RETURNED, 0, values.size());
    }
    
    @Test
    public void testExecuteQuery_2()
    {
        String queryStr = SELECT_CLASS_ID_FROM_SAMPLE_CLASS_WHERE_CLASS_ID_P1;
        SQLQuery query = new SQLQuery(getSession(), queryStr);
        List<Integer> values = query.execute(P1, "4");
        Assert.assertEquals(NO_VALUES_RETURNED, 0, values.size());
        
        SampleClassJDO sample = new SampleClassJDO();
        sample.setLastUpdateAt(new Date());
        sample.setName("Random-name-" + Math.random());
        DataService service = DataService.getDataService();
        service.save(sample);
        
        values = query.execute(P1, "1");
        Assert.assertEquals(VALUES_RETURNED, 1, values.size());
        
        values = query.execute(P1, "4");
        Assert.assertEquals(VALUES_RETURNED, 0, values.size());
    }
    
    @Test
    public void testExecuteQuery_3()
    {
        String queryStr = SELECT_CLASS_ID_FROM_SAMPLE_CLASS;
        SQLQuery query = new SQLQuery(getSession(), queryStr);
        // Make sure we can handle null parameter map
        List<Integer> values = query.execute(null);
        Assert.assertEquals(NO_VALUES_RETURNED, 0, values.size());
    }
    
    @Test
    public void testExecuteQuery_4()
    {
        String queryStr = SELECT_CLASS_ID_FROM_SAMPLE_CLASS_WHERE_CLASS_ID_P1;
        SQLQuery query = new SQLQuery(getSession(), queryStr);
        try
        {
            query.execute(null);
            Assert.fail("An exception should have been thrown for unresolved parameter");
        }
        catch (QueryException e)
        {
            // This is expected
        }
    }
}
