package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Immutable value object for monetary amounts.
 * Supports addition, subtraction, and multiplication.
 */
@Value
public class Money {

    private static final String DEFAULT_CURRENCY = "USD";

    BigDecimal amount;
    String currency;

    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }

    public Money(BigDecimal amount, String currency) {
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.currency = currency != null ? currency : DEFAULT_CURRENCY;
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                    "currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }
}
