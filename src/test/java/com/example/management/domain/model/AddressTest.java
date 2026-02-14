package com.example.management.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void shouldCreateAddressWithAllFields() {
        var address = new Address("123 Main St", "New York", "10001", "USA");
        assertThat(address.street()).isEqualTo("123 Main St");
        assertThat(address.city()).isEqualTo("New York");
        assertThat(address.zipCode()).isEqualTo("10001");
        assertThat(address.country()).isEqualTo("USA");
    }

    @Test
    void shouldBeEqualWhenSameValues() {
        var a = new Address("Street", "City", "ZIP", "Country");
        var b = new Address("Street", "City", "ZIP", "Country");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
