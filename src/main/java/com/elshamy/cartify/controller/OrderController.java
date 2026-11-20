package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.OrderDTO;
import com.elshamy.cartify.model.OrderStatus;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.service.order.IOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO.OrderResponse> checkout(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody OrderDTO.CheckoutRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(currentUser.getId(), request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO.OrderResponse>> getUserOrders(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(currentUser.getId(), pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO.OrderResponse> getOrderById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(currentUser.getId(), orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO.OrderResponse> cancelOrder(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(currentUser.getId(), orderId));
    }

    // =========================================================================
    // Admin Endpoints
    // =========================================================================

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO.OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(status, pageable));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO.OrderResponse> updateOrderStatus(
            @PathVariable @Positive Long orderId,
            @Valid @RequestBody OrderDTO.UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }
}