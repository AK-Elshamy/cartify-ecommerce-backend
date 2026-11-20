package com.elshamy.cartify.repository;

import com.elshamy.cartify.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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


    @Query("SELECT p FROM User u INNER JOIN u.wishlist p WHERE u.id = :userId")
    Page<Product> findWishlistByUserId(@Param("userId") Long userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

}