package com.elshamy.cartify.service.cart;

import com.elshamy.cartify.dtos.CartItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICartItem {

    CartItemDTO.CartItemDetailResponse addItem(
            Long userId,
            CartItemDTO.AddItemRequest request
    );

    CartItemDTO.CartItemDetailResponse updateItemQuantity(
            Long userId,
            Long productId,
            CartItemDTO.UpdateQuantityRequest request
    );

    void removeItem(Long userId, Long productId);

    Page<CartItemDTO.CartItemDetailResponse> getItems(
            Long userId,
            Pageable pageable
    );
}
