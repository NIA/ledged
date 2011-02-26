package ru.nia.jledger.core;

public interface TransactionHandler {
    void start(String date, String description);
    void addField(String account, String amount);
    void finish();
}
