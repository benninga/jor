package org.jor.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NamingUtils
{
    public static final String COPY_PREFIX = "Copy of ";
    public static final String DATE_PATTER = "YYYY-MM-dd HH:mm";
    
    public static String getCopiedObjectName(String originalName)
    {
        return COPY_PREFIX + originalName + " - " + new SimpleDateFormat(DATE_PATTER).format(new Date());
    }
}
