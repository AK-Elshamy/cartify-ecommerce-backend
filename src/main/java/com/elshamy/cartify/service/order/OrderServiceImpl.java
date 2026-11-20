package com.elshamy.cartify.service.order;

import com.elshamy.cartify.dtos.OrderDTO;
import com.elshamy.cartify.exception.InsufficientStockException;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.*;
import com.elshamy.cartify.repository.AddressRepository;
import com.elshamy.cartify.repository.CartRepository;
import com.elshamy.cartify.repository.OrderRepository;
import com.elshamy.cartify.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDTO.OrderResponse checkout(Long userId, OrderDTO.CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout with an empty cart");
        }

        Address address = addressRepository.findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + request.addressId()));

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(ShippingAddress.from(address));

        List<CartItem> sortedCartItems = cart.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .toList();

        for (CartItem cartItem : sortedCartItems) {
            Product lockedProduct = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + cartItem.getProduct().getId()
                    ));

            int requestedQty = cartItem.getQuantity();
            if (lockedProduct.getInventory() < requestedQty) {
                throw new InsufficientStockException(
                        "Insufficient stock for product '" + lockedProduct.getName() +
                                "'. Available: " + lockedProduct.getInventory() +
                                ", Requested: " + requestedQty
                );
            }

            lockedProduct.setInventory(lockedProduct.getInventory() - requestedQty);
            lockedProduct.setQuantity(lockedProduct.getQuantity() + requestedQty);
            productRepository.save(lockedProduct);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(lockedProduct);
            orderItem.setQuantity(requestedQty);
            orderItem.setUnitPrice(lockedProduct.getPrice());
            order.addItem(orderItem);
        }

        order.calculateTotalAmount();
        Order savedOrder = orderRepository.save(order);

        cart.clear();
        cartRepository.save(cart);

        return OrderDTO.OrderResponse.from(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO.OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderDTO.OrderResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO.OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return OrderDTO.OrderResponse.from(order);
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Cannot cancel order in status: " + order.getStatus()
            );
        }

        restoreInventory(order);
        order.setStatus(OrderStatus.CANCELLED);

        return OrderDTO.OrderResponse.from(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO.OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> orders = (status != null)
                ? orderRepository.findAllByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        return orders.map(OrderDTO.OrderResponse::from);
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse updateOrderStatus(Long orderId, OrderDTO.UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                    "Cannot change status of an order that is already " + order.getStatus()
            );
        }

        if (request.status() == OrderStatus.CANCELLED) {
            restoreInventory(order);
        }

        order.setStatus(request.status());
        return OrderDTO.OrderResponse.from(orderRepository.save(order));
    }

    // =========================================================================
    // Helper Methods
    // =========================================================================

    private void restoreInventory(Order order) {
        List<OrderItem> sortedItems = order.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .toList();

        for (OrderItem item : sortedItems) {
            Product lockedProduct = productRepository.findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + item.getProduct().getId()
                    ));

            lockedProduct.setInventory(lockedProduct.getInventory() + item.getQuantity());
            lockedProduct.setQuantity(Math.max(0, lockedProduct.getQuantity() - item.getQuantity()));
            productRepository.save(lockedProduct);
        }
    }
}