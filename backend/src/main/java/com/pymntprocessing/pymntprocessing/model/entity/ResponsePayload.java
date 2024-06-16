package com.pymntprocessing.pymntprocessing.model.entity;

public class ResponsePayload<T> {
    T payload;
    String message;

    boolean isSuccess;

    public ResponsePayload() {
    }

    public ResponsePayload(T payload, boolean isSuccess, String message) {
        this.payload = payload;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
