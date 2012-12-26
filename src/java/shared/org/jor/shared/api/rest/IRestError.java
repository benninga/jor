package org.jor.shared.api.rest;

import org.jor.shared.api.IApiInterface;

public interface IRestError extends IApiInterface
{
    int BAD_REQUEST = 400;
    int UNEXPECTED_ERROR = 1;
    int UNAUTHORIZED_ERROR = 11;
    int FORBIDDEN_ERROR = 12;
    int BAD_JSON = 20;
    int JSON_OUT_OF_SYNC = 20;
    int COMPONENTS_IN_USE = 500;
    
    Integer getHttpErrorCode();
    void setHttpErrorCode(Integer httpErrorCode);
    Integer getInternalErrorCode();
    void setInternalErrorCode(Integer internalErrorCode);
    String getMessage();
    void setMessage(String message);
}
