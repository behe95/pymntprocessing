package com.pymntprocessing.pymntprocessing.constant.db;

public enum InvoiceStatusValue {
    OPEN("Open"),
    COMMITTED("Committed"),
    LISTED("Listed"),
    BATCHED("Batched"),
    POSTED("Posted"),
    PAID("Paid");

    private final String statusValue;

    InvoiceStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    @Override
    public String toString() {
        return statusValue;
    }
}
