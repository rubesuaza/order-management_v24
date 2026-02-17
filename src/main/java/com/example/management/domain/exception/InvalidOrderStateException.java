package com.example.management.domain.exception;

/**
 * Exception thrown when attempting an invalid state transition on an Order.
 */
public class InvalidOrderStateException extends DomainException {
    
    public InvalidOrderStateException(String message) {
        super(message);
    }

    public InvalidOrderStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
