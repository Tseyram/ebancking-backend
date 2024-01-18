package com.ey.ebanckingbackend.exceptions;

public class BalanceNotSufficientException extends Exception {

    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
