package com.elshamy.cartify.service.cart;

import com.elshamy.cartify.dtos.CartItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CartItemImpl implements ICartItem {

    @Override
    public CartItemDTO.CartItemResponse addItem(CartItemDTO.AddItemRequest addItemRequest) {

    }

    @Override
    public CartItemDTO.CartItemResponse updateItemQuantity(Long productId, CartItemDTO.UpdateQuantityRequest updateItemQuantityRequest) {
        return null;
    }

    @Override
    public void removeItem(Long CartItemId) {

    }

    @Override
    public Page<CartItemDTO.CartItemDetailResponse> getItems(Long cartId, Pageable pageable) {
        return null;
    }
}