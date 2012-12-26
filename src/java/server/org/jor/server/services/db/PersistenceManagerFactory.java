package org.jor.server.services.db;

public class PersistenceManagerFactory
{
    public static PersistenceManagerFactory getPersistenceManagerFactory()
    {
        return new PersistenceManagerFactory();
    }
    
    public PersistenceManager getPersistenceManager(String serviceName)
    {
        return new PersistenceManager(serviceName);
    }
}
