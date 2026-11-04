package com.elshamy.cartify.dtos;

import com.elshamy.cartify.model.Category;
import com.elshamy.cartify.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;

public class CategoryDTO {

    public record CreateCategoryRequest(
            @NotBlank(message = "Category name is required")
            @Size(min = 3, max = 50,
                    message = "Category name must be between 3 and 50 characters")
            String name
    ) {
    }

    public record UpdateCategoryRequest(
            @NotBlank(message = "Category name is required")
            @Size(min = 3, max = 50,
                    message = "Category name must be between 3 and 50 characters")
            String name
    ) {
    }

    public record CategoryProductsResponse(
            Long id,
            String name,
            PageResponse<ProductDTO.ProductResponse> products
    ) {
        public static CategoryProductsResponse from(Category category
                , Page<Product> productsPage) {
            Page<ProductDTO.ProductResponse> productResponse = productsPage.map(
                    ProductDTO.ProductResponse::from
            );

            return new CategoryProductsResponse(
                    category.getId(),
                    category.getName(),
                    PageResponse.from(productResponse)
            );
        }
    }

    public record CategoryDTOResponse(
            Long id,
            String name
    ) {
        public static CategoryDTOResponse from(Category category) {
            return new CategoryDTOResponse(
                    category.getId(),
                    category.getName()
            );
        }
    }
}