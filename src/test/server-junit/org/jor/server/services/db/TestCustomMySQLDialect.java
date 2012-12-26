package org.jor.server.services.db;

import org.junit.Test;

public class TestCustomMySQLDialect
{
    @Test public void simpleTest()
    {
        CustomMySQLDialect c = new CustomMySQLDialect();
        c.getTableTypeString();
    }
}
