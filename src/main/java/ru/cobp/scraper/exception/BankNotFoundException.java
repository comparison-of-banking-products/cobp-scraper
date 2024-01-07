package ru.cobp.scraper.exception;

public class BankNotFoundException extends RuntimeException {

    public BankNotFoundException(String message) {
        super(message);
    }

}
