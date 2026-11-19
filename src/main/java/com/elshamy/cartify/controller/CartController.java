package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.CartDTO;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.service.cart.ICart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<CartDTO.CartResponse> getCart(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(cartService.getCart(currentUser.getId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User currentUser) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}