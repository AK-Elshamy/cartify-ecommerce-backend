package com.elshamy.cartify.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Namespace wrapper for Cart Item Data Transfer Objects (DTOs).
 */
public final class CartItemDTO {

    private CartItemDTO() {
        throw new UnsupportedOperationException("Utility DTO class cannot be instantiated");
    }

    // =========================================================================
    // Requests
    // =========================================================================

    /**
     * Request payload to add a product to the cart.
     */
    public record AddItemRequest(
            @NotNull(message = "Product ID is required")
            @Min(value = 1, message = "Product ID must be greater than 0")
            @JsonProperty("productId")
            Long productId,

            @NotNull(message = "Quantity is required")
            @Min(value = 1, message = "Quantity must be at least 1")
            @JsonProperty("quantity")
            Integer quantity
    ) {
        /**
         * Compact constructor guarantees validation rules even when instantiated programmatically
         * outside of web controllers (e.g., in unit tests or service code).
         */
        public AddItemRequest {
            if (productId != null && productId < 1) {
                throw new IllegalArgumentException("productId must be positive");
            }
            if (quantity != null && quantity < 1) {
                throw new IllegalArgumentException("quantity must be at least 1");
            }
        }
    }

    /**
     * Request payload to update quantity of an existing item (used with PUT /api/cart/items/{productId}).
     */
    public record UpdateQuantityRequest(
            @NotNull(message = "Quantity is required")
            @Min(value = 1, message = "Quantity must be at least 1")
            @JsonProperty("quantity")
            Integer quantity
    ) {
        public UpdateQuantityRequest {
            if (quantity != null && quantity < 1) {
                throw new IllegalArgumentException("quantity must be at least 1");
            }
        }
    }

    // =========================================================================
    // Responses
    // =========================================================================

    /**
     * Standard Cart Item Response.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CartItemResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("unitPrice") BigDecimal unitPrice
    ) {
        public CartItemResponse {
            if (unitPrice != null) {
                unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
            }
        }

        /**
         * Derived calculated property for total price (unitPrice * quantity).
         * Prevents storing redundant state or calculation mismatch.
         */
        @JsonProperty("totalPrice")
        public BigDecimal totalPrice() {
            return unitPrice != null
                    ? unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
        }
    }

    /**
     * Detailed Cart Item Response including nested product metadata.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CartItemDetailResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("unitPrice") BigDecimal unitPrice,
            @JsonProperty("product") ProductDTO.ProductResponse product
    ) {
        public CartItemDetailResponse {
            if (unitPrice != null) {
                unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
            }
        }

        @JsonProperty("totalPrice")
        public BigDecimal totalPrice() {
            return unitPrice != null
                    ? unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
        }
    }
}