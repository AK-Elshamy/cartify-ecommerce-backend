package com.elshamy.cartify.service.cart;

import com.elshamy.cartify.dtos.CartDTO;
import com.elshamy.cartify.model.Cart;

public interface ICart {
    Cart getOrCreateCart(Long userId);

    CartDTO.CartResponse getCart(Long userId);


    void clearCart(Long userId);
}