package org.jor.server.services.db;

import org.hibernate.Session;
import org.hibernate.TransientObjectException;

public class Transaction
{
    private Session session;
    private org.hibernate.Transaction transaction;
    
    Transaction(Session session)
    {
        this.session = session;
        this.transaction = session.getTransaction();
    }
    
    public void begin()
    {
        transaction.begin();
    }
    
    public void commit()
    {
        try
        {
            session.flush();
            transaction.commit();
        }
        catch (TransientObjectException e)
        {
            throw new InvalidObjectException(e);
        }
    }
    
    public void rollback()
    {
        transaction.rollback();
    }
    
    public boolean isActive()
    {
        return transaction.isActive();
    }
    
    protected org.hibernate.Transaction getInternalTransaction()
    {
        return this.transaction;
    }
}
