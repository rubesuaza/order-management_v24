package com.example.management.domain.model;

import lombok.Value;

/**
 * Immutable value object for a physical address.
 */
@Value
public class Address {

    String street;
    String city;
    String zipCode;
    String country;

    public Address(String street, String city, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }
}
