package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;

public class InvalidDataProvidedException extends GlobalErrorException{
    public InvalidDataProvidedException() {
        super(ErrorCodes.BAD_REQUEST.getErrorCode(), "Invalid data provided!");
    }
    public InvalidDataProvidedException(String message) {
        super(ErrorCodes.BAD_REQUEST.getErrorCode(), message);
    }
}
