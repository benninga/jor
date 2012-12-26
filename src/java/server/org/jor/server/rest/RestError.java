package org.jor.server.rest;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import org.jor.model.ModelBean;
import org.jor.shared.api.rest.IRestError;
import org.jor.utils.HtmlPrintable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonIgnoreProperties({"error", "httpStatusCode"})
@ModelBean(value=IRestError.class, usedForRest=false)
public class RestError implements IRestError, Serializable, HtmlPrintable
{
    private static final long serialVersionUID = 1L;
    
    private static final String NEW_LINE = "\n";
    
    public static final String BAD_JSON_MESSAGE = "Bad Json string.";
    public static final String JSON_STRING_IS_OUT_OF_SYNC_WITH_MODEL = "JSON string is out of sync with model.";
    public static final String USERNAME_OR_PASSWORD_INCORRECT = "Username or password incorrect";
    
    private Status httpStatusCode;
    private int internalErrorCode;
    private String message;
    private Throwable error;

    public RestError()
    {
        // Required for serialization
        httpStatusCode = Status.OK;
    }

    public RestError(Status httpErrorCode, int internalErrorCode, String message)
    {
        this(httpErrorCode, internalErrorCode, message, null);
    }

    public RestError(Status httpErrorCode, int internalErrorCode, Throwable error)
    {
        this(httpErrorCode, internalErrorCode, error.getMessage(), error);
    }

    public RestError(Status httpStatusCode, int internalErrorCode, String message, Throwable error)
    {
        this.httpStatusCode = httpStatusCode;
        this.internalErrorCode = internalErrorCode;
        this.message = message;
        if (message == null)
        {
            this.message = "Unexpected server error, see server logs";
        }
        this.error = error;
        
        if (httpStatusCode == null) {
            throw new NullPointerException("Status cannot be null");
        }
    }
    
    public RestError(RestError other)
    {
        this.httpStatusCode = other.getHttpStatusCode();
        this.internalErrorCode = other.getInternalErrorCode();
        this.message = other.getMessage();
        this.error = other.getError();
    }

    public Status getHttpStatusCode()
    {
        return httpStatusCode;
    }
    
    public void setHttpStatusCode(Status httpStatusCode)
    {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public Integer getHttpErrorCode()
    {
        return httpStatusCode.getStatusCode();
    }

    @Override
    public void setHttpErrorCode(Integer httpErrorCode)
    {
        Status status = Status.fromStatusCode(httpErrorCode);
        if (status == null) {
            throw new IllegalArgumentException("Unknown http error code: " + httpErrorCode);
        }
        this.httpStatusCode = status;
    }

    @Override
    public Integer getInternalErrorCode()
    {
        return internalErrorCode;
    }

    @Override
    public void setInternalErrorCode(Integer internalErrorCode)
    {
        this.internalErrorCode = internalErrorCode;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public void setError(Throwable error)
    {
        this.error = error;
    }
    
    @XmlTransient
    public Throwable getError()
    {
        return error;
    }
    
    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("HTTP Error: ").append(getHttpErrorCode()).append(NEW_LINE);
        b.append("Internal Error Code: ").append(getInternalErrorCode()).append(NEW_LINE);
        b.append("Message: ").append(getMessage()).append(NEW_LINE);
        
        if (error != null)
        {
            StringWriter s = new StringWriter();
            PrintWriter w = new PrintWriter(s);
            error.printStackTrace(w);
            b.append(s.toString());
        }
        return b.toString();
    }

    @Override
    public String toHtml()
    {
        StringBuilder b = new StringBuilder();

        b.append("<html>\n");
        b.append("<body>\n");
        
        Status status = getHttpStatusCode();
        b.append("<h1>HTTP Error: ").append(status.getStatusCode())
         .append(" ").append(status.getReasonPhrase()).append("</h1>").append(NEW_LINE);
        b.append("<h2>Internal Error Code: ").append(getInternalErrorCode()).append("</h2>").append(NEW_LINE);
        b.append("<h2>Message: ").append(getMessage()).append("</h2>").append(NEW_LINE);
        b.append("<br/>\n");
        
        if (error != null)
        {
            StringWriter s = new StringWriter();
            PrintWriter w = new PrintWriter(s);
            error.printStackTrace(w);
            b.append(s.toString());
        }
        b.append("</body>\n");
        b.append("</html>\n");
        return b.toString();
    }
}
