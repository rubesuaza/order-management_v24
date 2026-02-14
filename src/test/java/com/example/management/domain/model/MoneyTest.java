package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithDefaultCurrency() {
        var money = new Money(new BigDecimal("10.00"));
        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    void shouldCreateMoneyWithExplicitCurrency() {
        var money = new Money(new BigDecimal("5.50"), "EUR");
        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(money.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldAddSameCurrency() {
        var a = new Money(new BigDecimal("10.00"), "USD");
        var b = new Money(new BigDecimal("5.50"), "USD");
        var result = a.add(b);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("15.50"));
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowWhenAddingDifferentCurrencies() {
        var a = new Money(new BigDecimal("10.00"), "USD");
        var b = new Money(new BigDecimal("5.50"), "EUR");
        assertThatThrownBy(() -> a.add(b))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("mismatch");
    }

    @Test
    void shouldSubtractSameCurrency() {
        var a = new Money(new BigDecimal("20.00"), "USD");
        var b = new Money(new BigDecimal("7.25"), "USD");
        var result = a.subtract(b);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("12.75"));
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowWhenSubtractingDifferentCurrencies() {
        var a = new Money(new BigDecimal("20.00"), "USD");
        var b = new Money(new BigDecimal("7.25"), "GBP");
        assertThatThrownBy(() -> a.subtract(b))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldMultiplyByScalar() {
        var money = new Money(new BigDecimal("10.00"), "USD");
        var result = money.multiply(3);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(result.currency()).isEqualTo("USD");
    }

    @Test
    void shouldMultiplyByDecimalScalar() {
        var money = new Money(new BigDecimal("10.50"), "USD");
        var result = money.multiply(2);
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("21.00"));
    }
}
