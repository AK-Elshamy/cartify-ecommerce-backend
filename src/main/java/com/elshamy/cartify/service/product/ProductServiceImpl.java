package com.elshamy.cartify.service.product;

import com.elshamy.cartify.dtos.ProductDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Product;
import com.elshamy.cartify.repository.CategoryRepository;
import com.elshamy.cartify.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public ProductDTO.ProductResponse getProductById(Long id) {
        var product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductDTO.ProductResponse.from(product);
    }

    @Override
    public ProductDTO.ProductResponse addProduct(ProductDTO.CreateProductRequest productRequest) {
        var category = categoryRepository.findById(productRequest.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.categoryId()));
        Product product = new Product();
        product.setCategory(category);
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setDescription(productRequest.description());
        product.setBrand(productRequest.brand());
        product.setQuantity(productRequest.quantity());
        product.setInventory(productRequest.inventory());

        var productSaved = productRepository.save(product);
        return ProductDTO.ProductResponse.from(productSaved);

    }

    @Transactional
    @Override
    public ProductDTO.ProductResponse updateProduct(ProductDTO.UpdateProductRequest productRequest, Long id) {
        var product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (productRequest.name() != null) {
            product.setName(productRequest.name());
        }
        if (productRequest.description() != null) {
            product.setDescription(productRequest.description());
        }
        if (productRequest.brand() != null) {
            product.setBrand(productRequest.brand());
        }
        if (productRequest.quantity() != null) {
            product.setQuantity(productRequest.quantity());
        }
        if (productRequest.inventory() != null) {
            product.setInventory(productRequest.inventory());
        }
        if (productRequest.categoryId() != null) {
            var category = categoryRepository.findById(productRequest.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequest.categoryId()));

            product.setCategory(category);
        }
        if (productRequest.price() != null) {
            product.setPrice(productRequest.price());
        }

        var productSaved = productRepository.save(product);
        return ProductDTO.ProductResponse.from(productSaved);
    }

    @Override
    public void deleteProduct(Long id) {
        var product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductDTO.ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(ProductDTO.ProductResponse::from).toList();
    }

    @Override
    public List<ProductDTO.ProductResponse> getProductByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream().map(ProductDTO.ProductResponse::from).toList();
    }

    @Override
    public List<ProductDTO.ProductResponse> getProductByBrand(String brand) {
        return productRepository.findByBrand(brand).stream().map(ProductDTO.ProductResponse::from).toList();
    }

    @Override
    public List<ProductDTO.ProductResponse> getProductByCategoryAndBrand(Long categoryId, String brand) {
        return productRepository.findByCategoryIdAndBrand(categoryId, brand).stream().map(ProductDTO.ProductResponse::from).toList();
    }

    @Override
    public List<ProductDTO.ProductResponse> getProductByName(String name) {
        return productRepository.findByName(name).stream().map(ProductDTO.ProductResponse::from).toList();
    }

    @Override
    public List<ProductDTO.ProductResponse> getProductByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name).stream().map(ProductDTO.ProductResponse::from).toList();
    }

    @Override
    public long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countProductsByBrandAndName(brand, name);
    }
}