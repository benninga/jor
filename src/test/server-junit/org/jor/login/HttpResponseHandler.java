package org.jor.login;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpResponseHandler implements InvocationHandler
{
    private List<Cookie> cookies = new ArrayList<>(10);
    private String redirectURL;
    private String mimeType;
    private String header;
    private MyOutputStream outputStream = new MyOutputStream();
    
    private static final String ADD_COOKIE = "addCookie";
    private static final String SEND_REDIRECT = "sendRedirect";
    private static final String ENCODE_REDIRECT_URL = "encodeRedirectURL";
    private static final String GET_OUTPUT_STREAM = "getOutputStream";
    private static final String SET_CONTENT_TYPE = "setContentType";
    private static final String GET_CONTENT_TYPE = "getContentType";
    private static final String SET_HEADER = "setHeader";
    private static final String GET_HEADER = "getHeader";
    private static final String SET_STATUS = "setStatus";
    private static final String SET_CONTENT_LENGTH = "setContentLength";
    private static final String RESET = "reset";
    private static final String ENCODE_URL = "encodeURL";
    private static final String GET_WRITER = "getWriter";
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        String methodName = method.getName();
        if(ADD_COOKIE.equals(methodName))
        {
            Cookie cookie= (Cookie)args[0];
            return cookies.add(cookie);
        }
        else if (SEND_REDIRECT.equals(methodName))
        {
            redirectURL = (String)args[0];
            return redirectURL;
        }
        else if (ENCODE_REDIRECT_URL.equals(methodName)
                || ENCODE_URL.equals(methodName))
        {
            redirectURL = (String)args[0];
            String encoded = URLEncoder.encode(redirectURL, "UTF-8");
            return encoded;
        }
        else if (GET_OUTPUT_STREAM.equals(methodName))
        {
            return outputStream;
        }
        else if (SET_CONTENT_TYPE.equals(methodName))
        {
            mimeType = (String)args[0];
            return null;
        }
        else if (GET_CONTENT_TYPE.equals(methodName))
        {
            return mimeType;
        }
        else if (SET_HEADER.equals(methodName))
        {
            header = (String)args[0];
            return null;
        }
        else if (GET_HEADER.equals(methodName))
        {
            return header;
        }
        else if (SET_CONTENT_LENGTH.equals(methodName))
        {
            // Ignore this operation
            return null;
        }
        else if (RESET.equals(methodName))
        {
            // Do nothing for now
            return null;
        }
        else if (SET_STATUS.equals(methodName))
        {
            // Do nothing for now
            return null;
        }
        else if (GET_WRITER.equals(methodName))
        {
            // Do nothing for now
            return new PrintWriter(outputStream);
        }
        throw new RuntimeException("Unsupported method: " + methodName);
    }
    
    public Cookie[] getCookies()
    {
        Cookie[] cookiesCopy = cookies.toArray(new Cookie[0]);
        return cookiesCopy;
    }
    
    public String getRedirectURL()
    {
        return redirectURL;
    }
    
    public ByteArrayOutputStream getOutputStream()
    {
        return outputStream.getStream();
    }
    
    public static HttpServletResponse createHttpResponse()
    {
        InvocationHandler handler = new HttpResponseHandler();
        HttpServletResponse response = (HttpServletResponse)
            Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),
                                   new Class<?>[] { HttpServletResponse.class }, handler);

        return response;
    }
    
    protected class MyOutputStream extends ServletOutputStream
    {
        private ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        @Override
        public void write(int b) throws IOException
        {
            stream.write(b);
        }
        
        protected ByteArrayOutputStream getStream()
        {
            return stream;
        }
    }
}
