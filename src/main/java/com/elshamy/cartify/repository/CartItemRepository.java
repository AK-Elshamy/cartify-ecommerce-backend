package com.elshamy.cartify.repository;


import com.elshamy.cartify.model.CartItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    Page<CartItem> findByCartId(Long cartId, Pageable pageable);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

}