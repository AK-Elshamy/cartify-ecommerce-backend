package com.elshamy.cartify.dtos;

import com.elshamy.cartify.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public final class CartDTO {

    private CartDTO() {
        throw new UnsupportedOperationException(
                "Utility DTO class cannot be instantiated"
        );
    }

    public record CartResponse(
            Long id,
            List<CartItemDTO.CartItemDetailResponse> items,
            BigDecimal totalPrice
    ) {
        public CartResponse {
            items = items != null ? List.copyOf(items) : List.of();
        }

        public static CartResponse from(Cart cart) {
            return new CartResponse(
                    cart.getId(),
                    cart.getItems().stream()
                            .map(CartItemDTO.CartItemDetailResponse::from)
                            .toList(),
                    cart.getTotalPrice()
            );
        }

        public int itemCount() {
            return items.size();
        }
    }
}
