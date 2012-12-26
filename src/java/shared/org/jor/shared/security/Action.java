package org.jor.shared.security;

public enum Action implements ActionConstants
{
    RESET_PASSWORD(USER, "ResetPassword"),
    REST_API_CALL(REST, "ApiCall"),
    
    CREATE(MODEL, "Create"),
    UPDATE(MODEL, "Update"),
    DELETE(MODEL, "Delete");
    
    private String group;
    private String actionName;
    
    private Action(String group, String actionName)
    {
        this.group = group;
        this.actionName = actionName;
    }
    
    public String getGroup()
    {
        return group;
    }
    
    public String getActionName()
    {
        return actionName;
    }
}
