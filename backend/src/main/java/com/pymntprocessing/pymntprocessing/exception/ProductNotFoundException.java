package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;

public class ProductNotFoundException extends GlobalErrorException{

    public ProductNotFoundException() {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), "Product doesn't exist!");
    }

    public ProductNotFoundException(String message) {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), message);
    }
}
