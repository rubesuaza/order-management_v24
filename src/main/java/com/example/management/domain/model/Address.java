package com.example.management.domain.model;

import java.util.Objects;

/**
 * Value object representing a physical address.
 */
public record Address(String street, String city, String zipCode, String country) {

    public Address {
        Objects.requireNonNull(street, "street must not be null");
        Objects.requireNonNull(city, "city must not be null");
        Objects.requireNonNull(zipCode, "zipCode must not be null");
        Objects.requireNonNull(country, "country must not be null");
    }
}
