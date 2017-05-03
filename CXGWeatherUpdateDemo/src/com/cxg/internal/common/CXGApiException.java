package com.cxg.internal.common;

/**
 * Created by CXG Groups Pvt Ltd., Singapore on 4/28/17.
 */

public class CXGApiException extends Exception {

    private String message;
    private Exception exception;

    CXGApiException() {
        super();
    }

    CXGApiException(String msg) {
        super(msg);
    }

    CXGApiException(Exception e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }
}

