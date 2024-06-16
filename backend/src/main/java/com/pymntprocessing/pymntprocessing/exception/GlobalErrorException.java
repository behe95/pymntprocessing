package com.pymntprocessing.pymntprocessing.exception;

import java.util.Arrays;

public class GlobalErrorException extends RuntimeException{

    private final int code;

    private final String message;

    public GlobalErrorException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return new StackTraceElement[0];
    }
}
