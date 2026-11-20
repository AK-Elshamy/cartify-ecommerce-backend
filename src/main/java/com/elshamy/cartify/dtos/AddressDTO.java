package com.elshamy.cartify.dtos;

import com.elshamy.cartify.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public final class AddressDTO {

    private AddressDTO() {
        throw new UnsupportedOperationException("Utility DTO class cannot be instantiated");
    }

    public record CreateAddressRequest(
            @NotBlank(message = "Street is required")
            String street,

            @NotBlank(message = "City is required")
            String city,

            @NotBlank(message = "State is required")
            String state,

            @NotBlank(message = "Postal code is required")
            String postalCode,

            @NotBlank(message = "Country is required")
            String country,

            @NotBlank(message = "Phone number is required")
            @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
            String phoneNumber,

            Boolean isDefault
    ) {
    }

    public record UpdateAddressRequest(
            @NotBlank(message = "Street is required")
            String street,

            @NotBlank(message = "City is required")
            String city,

            @NotBlank(message = "State is required")
            String state,

            @NotBlank(message = "Postal code is required")
            String postalCode,

            @NotBlank(message = "Country is required")
            String country,

            @NotBlank(message = "Phone number is required")
            @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
            String phoneNumber,

            Boolean isDefault
    ) {
    }

    public record AddressResponse(
            Long id,
            String street,
            String city,
            String state,
            String postalCode,
            String country,
            String phoneNumber,
            boolean isDefault
    ) {
        public static AddressResponse from(Address address) {
            return new AddressResponse(
                    address.getId(),
                    address.getStreet(),
                    address.getCity(),
                    address.getState(),
                    address.getPostalCode(),
                    address.getCountry(),
                    address.getPhoneNumber(),
                    address.isDefault()
            );
        }
    }
}