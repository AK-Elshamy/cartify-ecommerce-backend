package com.elshamy.cartify.dtos;

import com.elshamy.cartify.model.Product;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductDTO {

    public record CreateProductRequest(

            @NotBlank(message = "Product name is required")
            @Size(min = 3, max = 50,
                    message = "Product name must be between 3 and 50 characters")
            String name,

            @NotBlank(message = "Product description is required")
            @Size(min = 3, max = 255,
                    message = "Product description must be between 3 and 255 characters")
            String description,

            @NotNull(message = "Product price is required")
            @Positive(message = "Product price must be positive")
            @Digits(integer = 10, fraction = 2,
                    message = "Product price must have at most 2 decimal places")
            BigDecimal price,

            @NotNull(message = "Product quantity is required")
            @PositiveOrZero(message = "Product quantity cannot be negative")
            Integer quantity,

            @NotNull(message = "Product inventory is required")
            @PositiveOrZero(message = "Product inventory cannot be negative")
            Integer inventory,

            @NotBlank(message = "Product brand is required")
            @Size(min = 2, max = 50,
                    message = "Product brand must be between 2 and 50 characters")
            String brand,

            @NotNull(message = "Category ID is required")
            @Positive(message = "Category ID must be positive")
            Long categoryId

    ) {
    }

    public record UpdateProductRequest(

            @Size(min = 3, max = 50,
                    message = "Product name must be between 3 and 50 characters")
            String name,

            @Size(min = 3, max = 255,
                    message = "Product description must be between 3 and 255 characters")
            String description,

            @Positive(message = "Product price must be positive")
            @Digits(integer = 10, fraction = 2,
                    message = "Product price must have at most 2 decimal places")
            BigDecimal price,

            @PositiveOrZero(message = "Product quantity cannot be negative")
            Integer quantity,

            @PositiveOrZero(message = "Product inventory cannot be negative")
            Integer inventory,

            @Size(min = 2, max = 50,
                    message = "Product brand must be between 2 and 50 characters")
            String brand,

            @Positive(message = "Category ID must be positive")
            Long categoryId

    ) {
    }

    public record ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            int quantity,
            int inventory,
            String brand,
            Long categoryId,
            String categoryName
    ) {

        public static ProductResponse from(Product product) {
            return new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getInventory(),
                    product.getBrand(),
                    product.getCategory() != null
                            ? product.getCategory().getId()
                            : null,
                    product.getCategory() != null
                            ? product.getCategory().getName()
                            : null
            );
        }
    }

}