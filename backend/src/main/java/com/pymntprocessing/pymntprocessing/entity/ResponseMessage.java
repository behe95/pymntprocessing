package com.pymntprocessing.pymntprocessing.entity;

public class ResponseMessage <T> {
    T data;
    String message;

    boolean isSuccess;

    public ResponseMessage() {
    }

    public ResponseMessage(T data, boolean isSuccess, String message) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
