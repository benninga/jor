package org.jor.server.rest;

import org.jor.server.services.ServicesManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

public class TestFreeMarkerHelper
{
    @Before public void setUp()
    {
        ServicesManager.defaultInit();
    }
    
    @After public void tearDown()
    {
        ServicesManager.defaultInit();
    }
    
    @Test public void testEmptyConstructor()
    {
        // For code coverage
        new FreeMarkerHelper();
    }
    
    @Test public void testTemplateLoaderGetters() throws Exception
    {
        FreeMarkerHelper.setTemplateLoader(null);
        ServicesManager.setApplicationRealPath("Non-Existent-Directory-" + Math.random());
        try
        {
            FreeMarkerHelper.getTemplateLoader();
            Assert.fail("An IOException should have been thrown for illegal application path");
        }
        catch (IOException e)
        {
            // This is expected. We should have tried to open a non-existent folder.
        }
        
        ServicesManager.defaultInit();
        FreeMarkerHelper.setTemplateLoader(null);
        TemplateLoader loader = FreeMarkerHelper.getTemplateLoader();
        Assert.assertEquals("Default loader is the file loader", FileTemplateLoader.class, loader.getClass());
        
        // Note, if we got an exception here than there is a problem with the expected directory structure of 
        // the application.
        // Either ServicesManagaer does not have a good default for the application path (for testing)
        // Or the additional directories created by the FreeMarkerHelper are incorrect.
    }
    
    @Test public void testTemplateLoaderSetters() throws Exception
    {
        StringTemplateLoader loader = new StringTemplateLoader();
        FreeMarkerHelper.setTemplateLoader(loader);
        TemplateLoader returnedLoader = FreeMarkerHelper.getTemplateLoader();
        Assert.assertEquals("Returned loader was not the same object as the one that was set", loader, returnedLoader);
    }
    
    @Test public void testExceptionInTemplatesGeneration() throws Exception
    {
        StringTemplateLoader loader = new StringTemplateLoader();
        FreeMarkerHelper.setTemplateLoader(loader);
        try
        {
            FreeMarkerHelper.processTemplateFile("Fake-Template-Name", null);
            Assert.fail("A runtime exception should have been thrown for missing template");
        }
        catch (RuntimeException e)
        {
            // Expected
        }
    }
    
    @Test public void testTemplatesDirectory()
    {
        // This test is a sanity check for the directory structure in the war directory.

        // For Debugging, print the current working directory.
        File current = new File("");
        String currentDir = current.getAbsolutePath();
        System.err.println("Current working directory is: " + currentDir);
        
        String templateDir = FreeMarkerHelper.getTemplatesDirectory();
        File file = new File(templateDir);
        Assert.assertTrue("Templates directory should have existed", file.exists());
        Assert.assertTrue("Templates directory should be a directory", file.isDirectory());
    }
    
    @Test public void testGetConfiguration() throws Exception
    {
        StringTemplateLoader loader = new StringTemplateLoader();
        FreeMarkerHelper.setTemplateLoader(loader);
        Configuration cfg = FreeMarkerHelper.getConfiguration();

        TemplateLoader returnedLoader = cfg.getTemplateLoader();
        Assert.assertEquals("Returned loader was not the same object as the one that was set", loader, returnedLoader);
    }
    
    @Test public void testTemplateProcessing()
    {
        String parameter = "parameter";
        String parameterValue = "My-random-value-" + Math.random();
        Map<String, Object> root = new HashMap<>();
        root.put(parameter, parameterValue);
        
        String part1 = "Test template 2: (Template with ";
        String part2 = " which will be replaced)";
        String name1 = "template-name-1-" + Math.random();
        String name2 = "template-name-2-" + Math.random();
        String template1 = "Test template 1: (Template with no parameterization)";
        String template2 = part1 + "${" + parameter + "}" + part2;
        
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate(name1, template1);
        loader.putTemplate(name2, template2);
        FreeMarkerHelper.setTemplateLoader(loader);
        
        
        String result = FreeMarkerHelper.processTemplateFile(name1, root);
        Assert.assertEquals("Template should have matched", template1, result);
        
        result = FreeMarkerHelper.processTemplateFile(name2, root);
        Assert.assertFalse("Returned template should not have been equal", template2.equals(result));
        Assert.assertEquals("Template should have matched", part1 + parameterValue + part2, result);
    }
}
