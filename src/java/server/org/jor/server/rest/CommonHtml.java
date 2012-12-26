package org.jor.server.rest;

import org.jor.server.services.ServicesManager;

public class CommonHtml
{
    private static final String A_HREF_OPEN = " - <a href='";

    public static String getCommonHeader()
    {
        String baseURL = ServicesManager.getContextPath();
        
        StringBuilder b = new StringBuilder();
        b.append("<div class='header' id='top-header'>\n");
        b.append("    <a href='").append(baseURL).append("/'>Home</a>");
        addLink(b, "/api/v1/user' title='Users Management'>Users</a>");
        addLink(b, "/api/v1/project' title=''>Projects</a>");
        addLink(b, "/api/v1/component' title=''>Component Libraries</a>");
        addLink(b, "/api/v1/houseplan' title=''>House Plans</a>");
        addLink(b, "/api/v1/purchase' title=''>Purchase Offers</a>");
        
        b.append("\n - Other: ");
        appendDropDown(b);
        
        b.append("  <br/>\n  <hr/>\n");
        b.append("</div>");
        return b.toString();
    }
    
    private static void appendDropDown(StringBuilder b)
    {
        String baseURL = ServicesManager.getContextPath();
        b.append("  <select name='goToItem' onchange='location = ");
        b.append("\"").append(baseURL).append("\"");
        b.append(" + this.options[this.selectedIndex].value; /*return dropdown(this)*/'>\n");
        b.append("    <option value=''>Choose Link</option>\n");
        b.append("    <option value='/api/v1/wall'>Walls</option>\n");
        b.append("    <option value='/api/v1/window'>Windows</option>\n");
        b.append("    <option value='/api/v1/floor'>Floors</option>\n");
        b.append("    <option value='/api/v1/ceiling'>Ceilings</option>\n");
        b.append("    <option value='/api/v1/basementwall'>Basement Walls</option>\n");
        b.append("    <option value='/api/v1/door'>Doors</option>\n");
        b.append("    <option value='/api/v1/airconditioner'>Air Conditioners</option>\n");
        b.append("    <option value='/api/v1/furnace'>Furnaces</option>\n");
        b.append("  </select>\n");
    }
    
    private static void addLink(StringBuilder b, String text)
    {
        String baseURL = ServicesManager.getContextPath();
        b.append(A_HREF_OPEN).append(baseURL).append(text).append("</a>");
    }
    
    public static String getCommonCSS()
    {
        String commonCSS = "dashboard-style.css";
        return getCommonCSS(commonCSS);
    }
    
    public static String getCommonCSS(String cssFileName)
    {
        String baseURL = ServicesManager.getContextPath();
        String css =
            String.format("<link rel='stylesheet' href='%s/css/ftl/%s' type='text/css' />\n",
                          baseURL, cssFileName);
        
        return css;
    }
}
