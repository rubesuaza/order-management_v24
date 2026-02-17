package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing monetary amounts with currency.
 * Immutable and supports arithmetic operations.
 */
@Value
public class Money {
    BigDecimal amount;
    String currency;

    public Money(BigDecimal amount) {
        this(amount, "USD");
    }

    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank");
        }
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Adds another Money amount. Both must have the same currency.
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtracts another Money amount. Both must have the same currency.
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiplies this Money by an integer quantity.
     */
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(new BigDecimal(quantity)), this.currency);
    }

    /**
     * Multiplies this Money by a BigDecimal factor.
     */
    public Money multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Factor cannot be null");
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                    String.format("Cannot perform operation: currencies differ (%s vs %s)", 
                            this.currency, other.currency)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
