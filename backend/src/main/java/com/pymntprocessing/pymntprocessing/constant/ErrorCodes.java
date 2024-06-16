package com.pymntprocessing.pymntprocessing.constant;

public enum ErrorCodes {
    BAD_REQUEST(400)
    ,NOT_FOUND(404)
    ,INITERNAL_SERVER_ERROR(500);

    private final int errorCode;
    ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
