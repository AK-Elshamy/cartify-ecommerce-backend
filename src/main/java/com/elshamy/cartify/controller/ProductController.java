package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.ProductDTO;
import com.elshamy.cartify.service.product.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO.ProductResponse> addProduct(
            @Valid @RequestBody ProductDTO.CreateProductRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addProduct(request));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDTO.ProductResponse> updateProduct(
            @Valid @RequestBody ProductDTO.UpdateProductRequest request,
            @PathVariable @Positive Long productId) {

        return ResponseEntity.ok(
                productService.updateProduct(request, productId)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable @Positive Long productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO.ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping(params = {"categoryId", "brandName"})
    public ResponseEntity<List<ProductDTO.ProductResponse>> getProductsByCategoryAndBrand(
            @RequestParam @Positive Long categoryId,
            @RequestParam String brandName) {

        return ResponseEntity.ok(
                productService.getProductByCategoryAndBrand(
                        categoryId,
                        brandName
                )
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO.ProductResponse>> getProductsByCategory(
            @PathVariable @Positive Long categoryId) {

        return ResponseEntity.ok(
                productService.getProductByCategory(categoryId)
        );
    }

    @GetMapping("/brand/{brandName}")
    public ResponseEntity<List<ProductDTO.ProductResponse>> getProductsByBrand(
            @PathVariable String brandName) {

        return ResponseEntity.ok(
                productService.getProductByBrand(brandName)
        );
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<ProductDTO.ProductResponse>> getProductsByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                productService.getProductByName(name)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO.ProductResponse> getProductById(@PathVariable @Positive Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/brand-name")
    public ResponseEntity<List<ProductDTO.ProductResponse>> getProductByBrandAndName(
            @RequestParam String brandName,
            @RequestParam String name) {

        return ResponseEntity.ok(
                productService.getProductByBrandAndName(brandName, name)
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCountProductsByBrandAndName(
            @RequestParam String brandName,
            @RequestParam String name) {

        return ResponseEntity.ok(
                productService.countProductsByBrandAndName(brandName, name)
        );
    }
}
