package com.example.management.domain.exception;

/**
 * Exception thrown when attempting to perform monetary operations
 * with different currencies.
 */
public class CurrencyMismatchException extends DomainException {
    
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
