package com.example.management.domain.exception;

/**
 * Exception thrown when an OrderItem has invalid attributes
 * (e.g., zero or negative quantity, negative price).
 */
public class InvalidItemException extends DomainException {
    
    public InvalidItemException(String message) {
        super(message);
    }
}
