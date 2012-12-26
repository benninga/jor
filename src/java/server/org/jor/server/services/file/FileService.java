package org.jor.server.services.file;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.jor.utils.CacheMap;

public class FileService
{
    private static FileService instance = new FileService();
    private static FileService defaultService;
    
    private Map<String, FileEntry> cachedFiles;
    
    public static FileService get()
    {
        if(defaultService != null) {
            return defaultService;
        }
        return instance;
    }
    
    public static void set(FileService service)
    {
        defaultService = service;
    }
    
    public FileService()
    {
        cachedFiles = new CacheMap<>();
    }
    
    public String storeTemporaryFile(String name, String uuidPrefix, byte[] contents)
    {
        Date date = new Date();
        String uuid = uuidPrefix + " - " + date.toString();
        FileEntry entry = new FileEntry(name, contents);
        cachedFiles.put(uuid, entry);
        return uuid;
    }
    
    public String storeTemporaryFile(String name, byte[] contents)
    {
        String uuid = UUID.randomUUID().toString();
        FileEntry entry = new FileEntry(name, contents);
        cachedFiles.put(uuid, entry);
        return uuid;
    }
    
    public FileEntry getTemporaryFile(String uuid)
    {
        return cachedFiles.get(uuid);
    }
    
    public static class FileEntry
    {
        private String name;
        private byte[] contents;
        
        private FileEntry(String name, byte[] contents)
        {
            this.name = name;
            this.contents = contents;
        }
        
        public String getName()
        {
            return name;
        }
        
        public byte[] getContents()
        {
            return contents;
        }
    }
    
}
