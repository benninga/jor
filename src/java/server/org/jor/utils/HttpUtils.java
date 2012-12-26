package org.jor.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.jor.server.rest.RestError;
import org.jor.shared.api.rest.RestException;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

public class HttpUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    public static final String COMMA = ",";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String EQ = "=";

    public static byte[] httpPost(String urlPage, String data,
                           String produce, String accept, String cookieString) throws Exception
    {
        return httpPost(urlPage, data, produce, accept, null, cookieString);
    }
    
    public static byte[] httpPost(String urlPage, String data, String produce,
                           String accept, Map<String, String> urlParams, String cookieString) throws Exception
    {
        return httpPost(urlPage, data, produce, accept, urlParams, cookieString, null);
    }
    
    public static byte[] httpPost(String urlPage, String data, String produce,
                           String accept, Map<String, String> urlParams, String cookieString, String authentication) throws Exception
    {
        long startTime = System.currentTimeMillis();
        // Send data
        urlPage = buildUrl(urlPage, urlParams);
        URL url = new URL(urlPage);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection ();
        conn.setRequestProperty(CONTENT_TYPE, produce);
        conn.setRequestProperty(ACCEPT, accept);
        
        if (cookieString != null){
            conn.setRequestProperty("Cookie", cookieString);
        }
        if (authentication != null) {
            conn.setRequestProperty("Authorization", authentication);
        }

        // We must send some data (such as empty) to indicate a POST vs. GET operation.
        if (data  == null)
        {
            data = "";
        }

        conn.setDoOutput(true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int responseCode = 0;
        try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());)
        {
	        wr.write(data);
	        wr.flush();
	        
	        responseCode = conn.getResponseCode();
	        InputStream is;
	        if (responseCode < 400) {
	            is = conn.getInputStream();
	        } else {
	            is = conn.getErrorStream();
	        }
	        
	        // Get the response
	        try (BufferedInputStream rd = new BufferedInputStream(is);)
	        {
		        int next = 0;
		        while ( (next = rd.read()) != -1) {
		            out.write((byte)next);
		        }
		        long totalTime = System.currentTimeMillis() - startTime;
		        LOG.info("HTTP request time: " + totalTime + ", URL: " + urlPage);
	        }
        }
        
        byte[] bytes = out.toByteArray();
        if (responseCode >= 400)
        {
            String json = bytesToString(bytes);
            RestError error = JsonUtils.fromString(json, RestError.class);
            throw new RestException(error);
        }
        return bytes;
    }
    
    public static byte[] httpGet(String urlPage, String accept, String cookieString) throws Exception
    {
        return httpGet(urlPage, accept, null, cookieString);
    }
    
    public static byte[] httpGet(String urlPage, String accept,
                          Map<String, String> urlParams, String cookieString) throws Exception
    {
        long startTime = System.currentTimeMillis();
        // Send data
        urlPage = HttpUtils.buildUrl(urlPage, urlParams);
        
        URL url = new URL(urlPage);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("accept", accept);
        if (cookieString != null){
            conn.setRequestProperty("Cookie", cookieString);
        }
        
        // Get the response
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (BufferedInputStream rd = new BufferedInputStream(conn.getInputStream());)
        {
	        int next = 0;
	        while ( (next = rd.read()) != -1) {
	            out.write((byte)next);
	        }
	        long totalTime = System.currentTimeMillis() - startTime;
	        LOG.info("Retrieved html page time: " + totalTime + ", page: " + urlPage);
        }
        
        return out.toByteArray();
    }

    public static String buildUrl(String resourceURL, Map<String, String> urlParams)
    {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(resourceURL);
        String and = "?";
        if (urlParams != null)
        {
            for (Map.Entry<String, String> entry : urlParams.entrySet())
            {
                urlBuilder.append(and);
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                and = "&";
            }
        }
        
        String url = urlBuilder.toString();
        return url;
    }

    public static String bytesToString(byte[] bytes) throws IOException
    {
    	StringBuffer sb = new StringBuffer();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));)
        {
	        String line;
	        while ((line = rd.readLine()) != null)
	        {
	            sb.append(line);
	        }
        }
        String result = sb.toString();
        return result;
    }
}
