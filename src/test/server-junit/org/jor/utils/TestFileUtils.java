package org.jor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class TestFileUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(TestFileUtils.class);
    
    private static final String TEST_LINE = "line %s";
    private static final int LINE_COUNT = 10;
    
    private File file;
    
    @Before
    public void setUp() throws Exception
    {
        createTemporaryFile();
    }
    
    @After
    public void tearDown() throws Exception
    {
        deleteTemporaryFile();
    }
    
    @Test
    public void testFullFileRead() throws IOException
    {
        String textFile = FileUtils.readTextFile(file.getAbsolutePath());
        
        StringBuilder expectedText = new StringBuilder();
        for (int i = 0; i < LINE_COUNT; i ++)
        {
            expectedText.append(String.format(TEST_LINE, i)).append("\n");
        }
        Assert.assertEquals("Same file content", expectedText.toString(), textFile);
    }
    
    @Test
    public void testLineByLineRead() throws IOException
    {
        List<String> lines = FileUtils.readTextFileByRows(file.getAbsolutePath());
        Assert.assertEquals("We're expecting these many lines", LINE_COUNT, lines.size());
        
        for (int i = 0; i < lines.size(); i ++)
        {
            Assert.assertEquals("Correct line value: ", String.format(TEST_LINE, i), lines.get(i));
        }
    }
    
    @Test
    public void testCloseReader() throws IOException
    {
        // Verify we don't blow up on null reader
        FileUtils.closeReader(null);
    }
    
    private void createTemporaryFile() throws IOException
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String date = format.format(new Date());
        String fileName = "junit-test-file-utils-" + date + ".txt";
        file = new File(fileName);
        
        boolean newlyCreated = file.createNewFile();
        if (newlyCreated == false)
        {
            throw new IOException("Our unit test file already existed");
        }
        LOG.info("Created new JUnit test file: " + file.getAbsolutePath());
        
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));)
        {
	        for (int i = 0; i < LINE_COUNT; i ++)
	        {
	            writer.write(String.format(TEST_LINE, i));
	            writer.write("\n");
	        }
        }
    }
    
    private void deleteTemporaryFile() throws IOException
    {
        boolean deleted = file.delete();
        if (deleted == false)
        {
            throw new IOException("Temporary JUnit test file was not deleted");
        }
    }
}

