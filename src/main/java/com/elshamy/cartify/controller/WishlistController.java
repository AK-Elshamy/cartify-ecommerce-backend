package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.ProductDTO;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.service.wishlist.IWishlistService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final IWishlistService wishlistService;

    @PostMapping("/{productId}")
    public ResponseEntity<ProductDTO.ProductSummaryResponse> addToWishlist(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long productId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.addToWishlist(currentUser.getId(), productId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO.ProductSummaryResponse>> getWishlist(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(wishlistService.getWishlist(currentUser.getId(), pageable));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long productId
    ) {
        wishlistService.removeFromWishlist(currentUser.getId(), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWishlist(
            @AuthenticationPrincipal User currentUser
    ) {
        wishlistService.clearWishlist(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}