package com.example.management.domain.model;

import com.example.management.domain.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithDefaultCurrency() {
        Money money = new Money(new BigDecimal("100.50"));
        
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(money.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldCreateMoneyWithSpecificCurrency() {
        Money money = new Money(new BigDecimal("50.25"), "EUR");
        
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("50.25"));
        assertThat(money.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("50.50"));
        
        Money result = money1.add(money2);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("150.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("50.00"), "EUR");
        
        assertThatThrownBy(() -> money1.add(money2))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldSubtractMoneyWithSameCurrency() {
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("30.50"));
        
        Money result = money1.subtract(money2);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("69.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldThrowExceptionWhenSubtractingDifferentCurrencies() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("50.00"), "EUR");
        
        assertThatThrownBy(() -> money1.subtract(money2))
                .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldMultiplyMoneyByInteger() {
        Money money = new Money(new BigDecimal("10.50"));
        
        Money result = money.multiply(3);
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldMultiplyMoneyByBigDecimal() {
        Money money = new Money(new BigDecimal("10.00"));
        
        Money result = money.multiply(new BigDecimal("2.5"));
        
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    void shouldBeEqualWhenAmountAndCurrencyAreSame() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("100.00"), "USD");
        
        assertThat(money1).isEqualTo(money2);
    }

    @Test
    void shouldNotBeEqualWhenCurrencyDiffers() {
        Money money1 = new Money(new BigDecimal("100.00"), "USD");
        Money money2 = new Money(new BigDecimal("100.00"), "EUR");
        
        assertThat(money1).isNotEqualTo(money2);
    }
}
