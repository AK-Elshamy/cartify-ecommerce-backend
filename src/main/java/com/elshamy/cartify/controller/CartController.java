package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.CartDTO;
import com.elshamy.cartify.service.cart.ICart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")

@RequiredArgsConstructor
public class CartController {
    private final ICart cartService;

    @GetMapping
    public ResponseEntity<CartDTO.CartResponse> getCart() {
        // Temporary: userId will come from authenticated user later
        Long userId = 1L;
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        Long userId = 1L;
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

}