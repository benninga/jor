package org.jor.server.services.db;

import junit.framework.Assert;

import org.junit.Test;

import org.jor.server.services.db.SimplifiedQuery.Filter;
import org.jor.server.services.db.SimplifiedQuery.OP;
import org.jor.server.services.db.SimplifiedQuery.Param;

public class TestSimplifiedQuery
{
    @Test
    public void testParam()
    {
        String paramName = "param-name-" + Math.random();
        Param param = new Param(paramName);
        Assert.assertEquals("Same param name", paramName, param.getParamName());
    }
    
    @Test
    public void testFilter()
    {
        String paramName = "param-name-" + Math.random();
        String columnName = "column-name-" + Math.random();
        OP operand = OP.EQ;
        Filter filter = new Filter(columnName, operand, new Param(paramName));
        Assert.assertEquals("Same param name", paramName, filter.getParam().getParamName());
        String filterString = filter.getFilterString();
        Assert.assertNotNull("Filter string is not null", filterString);
    }
}
