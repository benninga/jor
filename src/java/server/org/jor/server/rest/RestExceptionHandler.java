package org.jor.server.rest;

import static org.jor.server.rest.RestError.BAD_JSON_MESSAGE;
import static org.jor.server.rest.RestError.JSON_STRING_IS_OUT_OF_SYNC_WITH_MODEL;
import static org.jor.server.rest.RestError.USERNAME_OR_PASSWORD_INCORRECT;
import static org.jor.shared.api.rest.IRestError.BAD_JSON;
import static org.jor.shared.api.rest.IRestError.BAD_REQUEST;
import static org.jor.shared.api.rest.IRestError.FORBIDDEN_ERROR;
import static org.jor.shared.api.rest.IRestError.JSON_OUT_OF_SYNC;
import static org.jor.shared.api.rest.IRestError.UNAUTHORIZED_ERROR;
import static org.jor.shared.api.rest.IRestError.UNEXPECTED_ERROR;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jor.server.services.user.LoginFailedException;
import org.jor.server.services.user.NoAccessException;
import org.jor.shared.log.Logger;
import org.jor.shared.log.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@Provider
public class RestExceptionHandler implements ExceptionMapper<Exception>
{
    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);
    
    private final static Map<Class<? extends Exception>, RestError> errors;
    static
    {
        errors = new HashMap<>();
        errors.put(JsonProcessingException.class, new RestError(Status.BAD_REQUEST, BAD_JSON, BAD_JSON_MESSAGE));
        errors.put(JsonParseException.class, new RestError(Status.BAD_REQUEST, BAD_JSON, BAD_JSON_MESSAGE));
        errors.put(JsonGenerationException.class, new RestError(Status.BAD_REQUEST, BAD_JSON, BAD_JSON_MESSAGE));
        errors.put(JsonMappingException.class, new RestError(Status.BAD_REQUEST, JSON_OUT_OF_SYNC, JSON_STRING_IS_OUT_OF_SYNC_WITH_MODEL));
        errors.put(UnrecognizedPropertyException.class, new RestError(Status.BAD_REQUEST, JSON_OUT_OF_SYNC, JSON_STRING_IS_OUT_OF_SYNC_WITH_MODEL));
        errors.put(LoginFailedException.class, new RestError(Status.UNAUTHORIZED, UNAUTHORIZED_ERROR, USERNAME_OR_PASSWORD_INCORRECT));
    }
    
    @Override
    public Response toResponse(Exception exception)
    {
        LOG.warn("REST request resulted in an error.", exception);
        RestError restError = errors.get(exception.getClass());
        if (exception instanceof WebApplicationException)
        {
            int statusCode = ((WebApplicationException)exception).getResponse().getStatus();
            Status status = Status.fromStatusCode(statusCode);
            if (status == null) {
                status = Status.BAD_REQUEST;
            }
            restError = new RestError(status, BAD_REQUEST, exception);
        }
        else if (exception instanceof JsonMappingException && exception.getCause() != null)
        {
            Throwable e = exception.getCause();
            while(e.getCause()!=null)
            {
                e = e.getCause();
            }
            restError = new RestError(Status.BAD_REQUEST, BAD_REQUEST, e.getMessage(), exception);
        }
        else if (exception instanceof NoAccessException)
        {
            String error = (exception.getMessage() != null) ? exception.getMessage() : "No Access Allowed";
            restError = new RestError(Status.FORBIDDEN, FORBIDDEN_ERROR, error);
        }
        else if (exception instanceof LoginFailedException)
        {
            String error = exception.getMessage() + ". User was: "
                         + ((LoginFailedException)exception).getUsername();
            restError = new RestError(Status.UNAUTHORIZED, UNAUTHORIZED_ERROR, error, exception);
        }
        else if (restError == null)
        {
            restError = new RestError(Status.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR, exception);
        }
        else 
        {
            restError = new RestError(restError);
            restError.setError(exception);
        }
        
        return Response.status(Response.Status.fromStatusCode(restError.getHttpErrorCode()))
                       .entity(restError).build();
    }
    
}
