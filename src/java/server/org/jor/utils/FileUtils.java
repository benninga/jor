package org.jor.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils
{
    public static final String UTF_8 = "UTF-8";

    public static String readTextFile(String fileName) throws IOException
    {
        List<String> lines = readTextFileByRows(fileName);
        StringBuilder builder = new StringBuilder();
        for (String line : lines)
        {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public static List<String> readTextFileByRows(String fileName) throws IOException
    {
        BufferedReader reader = null;
        
        try
        {
            reader = new BufferedReader(new FileReader(fileName));
            List<String> lines = new ArrayList<>(200);
            String line;
            while ( (line = reader.readLine()) != null)
            {
                lines.add(line);
            }
            return lines;
        }
        finally
        {
            closeReader(reader);
        }
    }
    
    protected static void closeReader(Reader reader) throws IOException
    {
        if (reader != null) {
            reader.close();
        }
    }
    
    public static byte[] createZipFile(String fileName, String content) throws IOException
    {
        byte[] bytes = content.getBytes(UTF_8);

        // Create the ZIP file
        ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
        try (ZipOutputStream zip = new ZipOutputStream(out);)
        {
	        // Add ZIP entry to output stream.
	        zip.putNextEntry(new ZipEntry(fileName));
	        
	        // Write the file contents
	        zip.write(bytes);
	
	        // Complete the entry
	        zip.closeEntry();
        }        
        return out.toByteArray();
    }
}
