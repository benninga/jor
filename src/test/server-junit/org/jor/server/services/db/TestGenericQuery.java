package org.jor.server.services.db;

import junit.framework.Assert;

import org.junit.Test;

public class TestGenericQuery extends AbstractQueryTest
{
    @Test
    public void testSetGetQueryString()
    {
        String queryStr = "query-string-" + Math.random();
        GenericQuery query = new GenericQuery(getSession(), queryStr);
        Assert.assertEquals("Same query string", queryStr, query.getQueryString());
    }
}
