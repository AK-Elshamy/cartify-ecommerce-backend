package com.elshamy.cartify.dtos;

import com.elshamy.cartify.model.OrderStatus;
import com.elshamy.cartify.model.Payment;
import com.elshamy.cartify.model.PaymentMethod;
import com.elshamy.cartify.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PaymentDTO {

    private PaymentDTO() {
        throw new UnsupportedOperationException("Utility DTO class cannot be instantiated");
    }

    public record InitiatePaymentRequest(
            @NotNull(message = "Order ID is required")
            @Positive(message = "Order ID must be positive")
            Long orderId,

            @NotNull(message = "Payment method is required")
            PaymentMethod paymentMethod,

            @NotBlank(message = "Idempotency key is required")
            String idempotencyKey
    ) {
    }

    public record WebhookRequest(
            @NotBlank(message = "Transaction reference is required")
            String transactionReference,

            @NotNull(message = "Payment status is required")
            PaymentStatus status
    ) {
    }

    public record PaymentResponse(
            Long id,
            Long orderId,
            String transactionReference,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            OrderStatus orderStatus,
            BigDecimal amount,
            String paymentUrl,
            LocalDateTime createdAt
    ) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.getId(),
                    payment.getOrder().getId(),
                    payment.getTransactionReference(),
                    payment.getPaymentMethod(),
                    payment.getStatus(),
                    payment.getOrder().getStatus(),
                    payment.getAmount(),
                    payment.getPaymentUrl(),
                    payment.getCreatedAt()
            );
        }
    }
}