package com.elshamy.cartify.dtos;

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

        public int itemCount() {
            return items.size();
        }
    }
}
