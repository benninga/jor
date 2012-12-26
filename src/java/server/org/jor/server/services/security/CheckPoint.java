package org.jor.server.services.security;

import org.jor.shared.security.Action;

import java.util.HashSet;
import java.util.Set;

public class CheckPoint
{
    private final Set<Action> actions;
    private final Set<String> resources;
    
    public CheckPoint()
    {
        this.actions = new HashSet<>();
        this.resources = new HashSet<>();
    }

    public CheckPoint addActions(Action ... newActions)
    {
        for (Action action : newActions)
        {
            if (action == null) {
                throw new NullPointerException("Cannot add null actions");
            }
            this.actions.add(action);
        }
        return this;
    }
    
    public CheckPoint addResources(String ... newResources)
    {
        for (String resource : newResources)
        {
            if (resource == null) {
                throw new NullPointerException("Cannot add null resources");
            }
            this.resources.add(resource);
        }
        return this;
    }
    
    public Set<Action> getActions()
    {
        return actions;
    }
    
    public Set<String> getResources()
    {
        return resources;
    }
}
