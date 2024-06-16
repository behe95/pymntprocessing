package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;

public class ProductAssignedWithInvalidPaymentTransactionException extends GlobalErrorException{


    public ProductAssignedWithInvalidPaymentTransactionException() {
        super(ErrorCodes.BAD_REQUEST.getErrorCode(), "Unable to assign payment transaction to product");
    }

    public ProductAssignedWithInvalidPaymentTransactionException(String message) {
        super(ErrorCodes.BAD_REQUEST.getErrorCode(), message);
    }
}
