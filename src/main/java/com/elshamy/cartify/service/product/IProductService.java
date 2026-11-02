package com.elshamy.cartify.service.product;

import com.elshamy.cartify.dtos.ProductDTO;

import java.util.List;

public interface IProductService {
    ProductDTO.ProductResponse getProduct(Long id);

    ProductDTO.ProductResponse addProduct(ProductDTO.CreateProductRequest product);

    ProductDTO.ProductResponse updateProduct(ProductDTO.UpdateProductRequest product, Long id);

    void deleteProduct(Long id);

    List<ProductDTO.ProductResponse> getProducts();

    List<ProductDTO.ProductResponse> getProductByCategory(Long categoryId);

    List<ProductDTO.ProductResponse> getProductByBrand(String brand);

    List<ProductDTO.ProductResponse> getProductByCategoryAndBrand(Long categoryId, String brand);

    List<ProductDTO.ProductResponse> getProductByName(String name);

    List<ProductDTO.ProductResponse> getProductByBrandAndName(String brand, String name);

    long countProductsByBrandAndName(String brand, String name);

}