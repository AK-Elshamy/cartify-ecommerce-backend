package com.elshamy.cartify.repository;

import com.elshamy.cartify.model.Order;
import com.elshamy.cartify.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findAll(Pageable pageable);
}