package com.elshamy.cartify.service.wishlist;

import com.elshamy.cartify.dtos.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IWishlistService {
    ProductDTO.ProductSummaryResponse addToWishlist(Long userId, Long productId);

    Page<ProductDTO.ProductSummaryResponse> getWishlist(Long userId, Pageable pageable);

    void removeFromWishlist(Long userId, Long productId);

    void clearWishlist(Long userId);
}