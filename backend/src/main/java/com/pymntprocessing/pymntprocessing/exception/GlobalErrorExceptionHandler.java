package com.pymntprocessing.pymntprocessing.exception;

import com.pymntprocessing.pymntprocessing.constant.ErrorCodes;
import com.pymntprocessing.pymntprocessing.model.entity.ResponsePayload;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorExceptionHandler {
    @ExceptionHandler(GlobalErrorException.class)
    public ResponseEntity<ResponsePayload<GlobalErrorException>> handleGlobalException(GlobalErrorException globalErrorException) {
        return ResponseEntity
                .status(globalErrorException.getCode())
                .body(new ResponsePayload<>(globalErrorException, false, globalErrorException.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponsePayload<Exception>> handleGenericException(Exception exception) {
        return ResponseEntity
                .status(ErrorCodes.INITERNAL_SERVER_ERROR.getErrorCode())
                .body(new ResponsePayload<>(null, false, ("Something went wrong! " + exception.getMessage()).trim()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponsePayload<DataIntegrityViolationException>> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String errorMessage = "ERROR: Duplicate entry! ";

        if (exception.getMessage() != null) {
            errorMessage += exception.getMessage();
        }

        if (exception.getCause() != null) {
            errorMessage += exception.getCause().getMessage();
        }

        if (exception.getRootCause() != null) {
            errorMessage += exception.getRootCause().getMessage();
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponsePayload<>(null, false, errorMessage.trim()));
    }
}
