package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;

public class VendorNotFoundException extends GlobalErrorException {
    public VendorNotFoundException() {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), "Vendor doesn't exist!");
    }

    public VendorNotFoundException(String message) {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), message);
    }
}
