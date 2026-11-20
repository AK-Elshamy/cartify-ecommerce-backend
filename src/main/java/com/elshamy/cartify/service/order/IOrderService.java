package com.elshamy.cartify.service.order;

import com.elshamy.cartify.dtos.OrderDTO;
import com.elshamy.cartify.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    OrderDTO.OrderResponse checkout(Long userId, OrderDTO.CheckoutRequest request);

    Page<OrderDTO.OrderResponse> getUserOrders(Long userId, Pageable pageable);

    OrderDTO.OrderResponse getOrderById(Long userId, Long orderId);

    OrderDTO.OrderResponse cancelOrder(Long userId, Long orderId);

    // Admin Operations
    Page<OrderDTO.OrderResponse> getAllOrders(OrderStatus status, Pageable pageable);

    OrderDTO.OrderResponse updateOrderStatus(Long orderId, OrderDTO.UpdateOrderStatusRequest request);
}