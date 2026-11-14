package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.CartItemDTO;
import com.elshamy.cartify.service.cart.ICartItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cart/items")
@RequiredArgsConstructor
@Validated
public class CartItemController {
    private final ICartItem cartItemService;

    @PostMapping
    public ResponseEntity<CartItemDTO.CartItemDetailResponse> addItem(
            @Valid @RequestBody CartItemDTO.AddItemRequest addItemRequest
    ) {
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addItem(userId, addItemRequest));
    }

    @GetMapping
    public ResponseEntity<Page<CartItemDTO.CartItemDetailResponse>> getItems(Pageable pageable) {
        Long userId = 1L;
        return ResponseEntity.ok(cartItemService.getItems(userId, pageable));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CartItemDTO.CartItemDetailResponse> updateItem(
            @PathVariable @Positive Long productId,
            @Valid @RequestBody CartItemDTO.UpdateQuantityRequest request
    ) {
        Long userId = 1L;
        var response = cartItemService.updateItemQuantity(userId, productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable @Positive Long productId) {
        Long userId = 1L;
        cartItemService.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }
}