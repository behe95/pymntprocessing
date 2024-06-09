package com.pymntprocessing.pymntprocessing.model;

public class ResponseMessage <T> {
    T data;
    String message;

    public ResponseMessage() {
    }

    public ResponseMessage(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
