package com.emc.coprhd.sp.dto;

import java.util.Map;

@SuppressWarnings("unused")
public class ErrorJson {
    private final Integer status;
    private final String error;
    private final String message;
    private final String timeStamp;
    private final Object exception;

    public ErrorJson(final int status, final Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.exception = errorAttributes.get("exception");
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Object getException() {
        return exception;
    }
}
