package com.elshamy.cartify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    @Column(name = "shipping_street", nullable = false)
    private String street;

    @Column(name = "shipping_city", nullable = false)
    private String city;

    @Column(name = "shipping_state", nullable = false)
    private String state;

    @Column(name = "shipping_postal_code", nullable = false)
    private String postalCode;

    @Column(name = "shipping_country", nullable = false)
    private String country;

    @Column(name = "shipping_phone", nullable = false)
    private String phoneNumber;

    public static ShippingAddress from(Address address) {
        return new ShippingAddress(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getPhoneNumber()
        );
    }
}