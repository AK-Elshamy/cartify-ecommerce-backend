package com.elshamy.cartify.repository;

import com.elshamy.cartify.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}