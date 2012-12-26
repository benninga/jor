package org.jor.shared.utils;

import java.util.Map;

import com.google.common.collect.Maps;

public class UrlUtils
{
    /**
     *
     * @param fragment A string of the format "blahBlah?foo=1&bar=baz"
     * @return blahBlah
     */
    public static String getToken(String fragment)
    {
        String token = null;

        if (fragment != null)
        {
            int queryStartIndex = fragment.indexOf('?');
            if (queryStartIndex != -1)
            {
                token = fragment.substring(0, queryStartIndex);
            }
            else
            {
                token = fragment;
            }
        }

        return token;
    }

    /**
     *
     * @param fragment A string of the format "blahBlah?foo=1&bar=baz"
     * @return foo=1&bar=baz
     */
    public static String getQueryString(String fragment)
    {
        String queryString = null;

        if (fragment != null)
        {
            int queryStartIndex = fragment.indexOf('?');
            if (queryStartIndex != -1)
            {
                // Excludes leading ?
                queryString = fragment.substring(queryStartIndex+1, fragment.length());
            }
        }
        return queryString;
    }

    /**
     *
     * @param fragment A string of the format "blahBlah?foo=1&bar=baz"
     */
    public static Map<String, String> getQueryParams(String fragment)
    {
        Map<String, String> params = Maps.newHashMap();

        String queryLeftToParse = getQueryString(fragment);

        if (queryLeftToParse != null)
        {
            while (queryLeftToParse.contains("&"))
            {
                int splitIndex = queryLeftToParse.indexOf('&');

                // Split from the start of the string to the first &
                String param = queryLeftToParse.substring(0, splitIndex);
                parseParamIntoMap(param, params);

                // keep from the character after the first & to the end of the string
                queryLeftToParse = queryLeftToParse.substring(splitIndex+1);
            }

            // Only one param pair left
            if (queryLeftToParse.contains("="))
            {
                parseParamIntoMap(queryLeftToParse, params);
            }
        }

        return params;
    }

    private static void parseParamIntoMap(String param, Map<String, String> params)
    {
        int splitIndex = param.indexOf('=');
        if (splitIndex != -1)
        {
            String key = param.substring(0, splitIndex);
            String value = param.substring(splitIndex+1);
            params.put(key, value);
        }
    }
}

