package com.pymntprocessing.pymntprocessing.constant.db;

public enum TransactionStatusValue {
    OPEN("Open"),
    COMMITTED("Committed"),
    LISTED("Listed"),
    BATCHED("Batched"),
    POSTED("Posted"),
    PAID("Paid");

    private final String statusValue;

    TransactionStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    @Override
    public String toString() {
        return statusValue;
    }
}
