package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;

public class InvoiceNotFoundException extends GlobalErrorException{
    public InvoiceNotFoundException() {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), "Invoice doesn't exist!");
    }

    public InvoiceNotFoundException(String message) {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), message);
    }
}
