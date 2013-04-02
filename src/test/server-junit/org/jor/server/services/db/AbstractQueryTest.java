package org.jor.server.services.db;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

import org.jor.jdo.BaseJdoTestClass;

public abstract class AbstractQueryTest extends BaseJdoTestClass
{
    private Session session;
    
    @Before @Override
    public void setUp() throws Exception
    {
        super.setUp();
        session = PersistenceManager.getSessionFactory().openSession();
    }
    
    @After @Override
    public void tearDown() throws Exception
    {
        session.close();
        super.tearDown();
    }
    
    protected final Session getSession()
    {
        return session;
    }
}
