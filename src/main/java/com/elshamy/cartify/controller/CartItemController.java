package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.CartItemDTO;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.service.cart.ICartItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/cart/items")
@RequiredArgsConstructor
public class CartItemController {

    private final ICartItem cartItemService;

    @PostMapping
    public ResponseEntity<CartItemDTO.CartItemDetailResponse> addItem(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CartItemDTO.AddItemRequest addItemRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartItemService.addItem(currentUser.getId(), addItemRequest));
    }

    @GetMapping
    public ResponseEntity<Page<CartItemDTO.CartItemDetailResponse>> getItems(
            @AuthenticationPrincipal User currentUser,
            Pageable pageable
    ) {
        return ResponseEntity.ok(cartItemService.getItems(currentUser.getId(), pageable));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CartItemDTO.CartItemDetailResponse> updateItem(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long productId,
            @Valid @RequestBody CartItemDTO.UpdateQuantityRequest request
    ) {
        var response = cartItemService.updateItemQuantity(currentUser.getId(), productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long productId
    ) {
        cartItemService.removeItem(currentUser.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}