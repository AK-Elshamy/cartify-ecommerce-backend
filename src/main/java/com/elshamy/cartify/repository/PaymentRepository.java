package com.elshamy.cartify.repository;

import com.elshamy.cartify.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionReference(String transactionReference);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    List<Payment> findByOrderIdAndOrderUserId(Long orderId, Long userId);
}