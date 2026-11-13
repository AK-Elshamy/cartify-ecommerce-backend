package com.elshamy.cartify.service.cart;

import com.elshamy.cartify.dtos.CartItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICartItem {
    CartItemDTO.CartItemResponse addItem(CartItemDTO.AddItemRequest addItemRequest);

    CartItemDTO.CartItemResponse updateItemQuantity(Long productId, CartItemDTO.UpdateQuantityRequest updateItemQuantityRequest);

    void removeItem(Long CartItemId);

    Page<CartItemDTO.CartItemDetailResponse> getItems(Long cartId, Pageable pageable);

}