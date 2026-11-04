package com.elshamy.cartify.repository;

import com.elshamy.cartify.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrand(String brand);

    List<Product> findByCategoryIdAndBrand(Long categoryId, String brand);

    List<Product> findByName(String name);

    List<Product> findByBrandAndName(String brand, String name);

    long countProductsByBrandAndName(String brand, String name);

    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

}