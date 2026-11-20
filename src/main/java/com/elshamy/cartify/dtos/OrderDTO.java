package com.elshamy.cartify.dtos;

import com.elshamy.cartify.model.Order;
import com.elshamy.cartify.model.OrderItem;
import com.elshamy.cartify.model.OrderStatus;
import com.elshamy.cartify.model.ShippingAddress;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderDTO {

    private OrderDTO() {
        throw new UnsupportedOperationException("Utility DTO class cannot be instantiated");
    }

    public record CheckoutRequest(
            @NotNull(message = "Shipping address ID is required")
            @Positive(message = "Address ID must be positive")
            Long addressId
    ) {
    }

    public record UpdateOrderStatusRequest(
            @NotNull(message = "Order status is required")
            OrderStatus status
    ) {
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
            );
        }
    }

    public record OrderResponse(
            Long id,
            LocalDateTime orderDate,
            OrderStatus status,
            BigDecimal totalAmount,
            ShippingAddress shippingAddress,
            List<OrderItemResponse> items
    ) {
        public static OrderResponse from(Order order) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(OrderItemResponse::from)
                    .toList();

            return new OrderResponse(
                    order.getId(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getShippingAddress(),
                    itemResponses
            );
        }
    }
}