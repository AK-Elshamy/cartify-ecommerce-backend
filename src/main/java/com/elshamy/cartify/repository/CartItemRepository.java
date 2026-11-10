package com.elshamy.cartify.repository;

import com.elshamy.cartify.model.CartItem;
import org.springframework.data.repository.CrudRepository;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {
}