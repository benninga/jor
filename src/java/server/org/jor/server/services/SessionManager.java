package org.jor.server.services;

import org.jor.server.services.user.SecurityContext;

import java.util.HashMap;
import java.util.Map;

public class SessionManager
{
    private static final SessionManager instance = new SessionManager();

    private Map<Thread, Session> sessions;
    
    public static SessionManager get()
    {
        return instance;
    }
    
    private SessionManager()
    {
        sessions = new HashMap<>();
    }
    
    public void createSession()
    {
        SecurityContext context = new SecurityContext();
        createSession(context);
    }

    public void createSession(SecurityContext context)
    {
        if (context == null) {
            throw new NullPointerException("Cannot create sesson with null security context");
        }
        Thread thread = Thread.currentThread();
        Session session = new Session(context);
        synchronized(sessions)
        {
            Session existing = sessions.get(thread);
            if (existing != null) {
                throw new IllegalStateException("Cannot create new session for current thread. Session already exists");
            }
            sessions.put(thread, session);
        }
    }
    
    public Session removeSession()
    {
        Thread thread = Thread.currentThread();
        synchronized(sessions)
        {
            Session existing = sessions.remove(thread);
            return existing;
        }
    }

    public Session getSession()
    {
        Thread thread = Thread.currentThread();
        Session session = sessions.get(thread);
        return session;
    }
    
    public void clear()
    {
        synchronized(sessions)
        {
            sessions.clear();
        }
    }
}
