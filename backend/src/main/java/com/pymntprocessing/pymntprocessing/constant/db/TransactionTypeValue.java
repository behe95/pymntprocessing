package com.pymntprocessing.pymntprocessing.constant.db;

public enum TransactionTypeValue {
    CREDIT("Credit", -1),
    DEBIT("Debit", 1);

    private final String typeValue;
    private final int multiplier;

    TransactionTypeValue(String typeValue, int multiplier) {
        this.typeValue = typeValue;
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return typeValue;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
