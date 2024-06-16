package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;

public class PaymentTransactionNotFoundException extends GlobalErrorException{
    public PaymentTransactionNotFoundException() {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), "Payment transaction doesn't exist!");
    }
    public PaymentTransactionNotFoundException(String message) {
        super(ErrorCodes.NOT_FOUND.getErrorCode(), message);
    }
}
