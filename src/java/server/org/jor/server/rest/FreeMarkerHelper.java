package org.jor.server.rest;

import org.jor.rest.RestConstants;
import org.jor.server.services.ServicesManager;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.Version;

public class FreeMarkerHelper implements RestConstants
{
    private static final Logger logger = LoggerFactory.getLogger(FreeMarkerHelper.class);
    private static TemplateLoader templateLoader;
    
    public static Configuration getConfiguration() throws IOException
    {
        Version version = Configuration.VERSION_2_3_22;
        Configuration cfg = new Configuration(version);
        cfg.setTemplateLoader(getTemplateLoader());
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(version);
        builder.setExposeFields(true);
        DefaultObjectWrapper wrapper = builder.build();
        cfg.setObjectWrapper(wrapper); 
        return cfg;
    }
    
    public static String processTemplateFile(String templateFileName,
                                             Map<String, Object> root)
    {
        root.put(BASE_URL, ServicesManager.getContextPath());
        
        try
        {
            Configuration cfg = getConfiguration();
            Template template = cfg.getTemplate(templateFileName);
            StringWriter out = new StringWriter();
            template.process(root, out);
            String page = out.toString();
            return page;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
    
    protected static String getTemplatesDirectory()
    {
        return ServicesManager.getApplicationRealPath() + File.separator + PAGES_TEMPLATES;
    }
    
    public static void setTemplateLoader(TemplateLoader templateLoader)
    {
        FreeMarkerHelper.templateLoader = templateLoader;
    }
    
    protected static TemplateLoader getTemplateLoader() throws IOException
    {
        if(templateLoader == null)
        {
            String templatesDirectory = getTemplatesDirectory();
            templateLoader = new FileTemplateLoader(new File(templatesDirectory));
        }
        return templateLoader;
    }
    
    public static Map<String, Object> replaceNullWithEmptyString(Map<String, Object> oldMap)
    {
        Map<String, Object> newMap = new HashMap<>();
        for (String key : oldMap.keySet())
        {
            Object value = oldMap.get(key);
            Object newValue = (value == null) ? "" : value.toString();
            newMap.put(key, newValue);
        }
        return newMap;
    }
}
