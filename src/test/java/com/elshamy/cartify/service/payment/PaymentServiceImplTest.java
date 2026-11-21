package com.elshamy.cartify.service.payment;

import com.elshamy.cartify.dtos.OrderDTO;
import com.elshamy.cartify.dtos.PaymentDTO;
import com.elshamy.cartify.model.*;
import com.elshamy.cartify.repository.OrderRepository;
import com.elshamy.cartify.repository.PaymentRepository;
import com.elshamy.cartify.service.order.IOrderService;
import com.elshamy.cartify.service.payment.strategy.CardPaymentStrategy;
import com.elshamy.cartify.service.payment.strategy.CashOnDeliveryStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private static final String WEBHOOK_SECRET = "cartify-webhook-secret-2026";
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private IOrderService orderService;
    private PaymentServiceImpl paymentService;
    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                paymentRepository,
                orderRepository,
                orderService,
                List.of(new CardPaymentStrategy(), new CashOnDeliveryStrategy())
        );
        ReflectionTestUtils.setField(paymentService, "expectedWebhookSecret", WEBHOOK_SECRET);

        user = new User();
        user.setId(1L);
        user.setEmail("sara@cartify.com");

        order = new Order();
        order.setId(10L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Should initiate card payment and generate checkout URL")
    void initiatePayment_Card_Success() {
        PaymentDTO.InitiatePaymentRequest request =
                new PaymentDTO.InitiatePaymentRequest(10L, PaymentMethod.CARD, "idem-key-1");

        when(paymentRepository.findByIdempotencyKey("idem-key-1")).thenReturn(Optional.empty());
        when(orderRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(50L);
            return p;
        });

        PaymentDTO.PaymentResponse response = paymentService.initiatePayment(1L, request);

        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.orderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.paymentUrl()).startsWith("https://checkout.cartify-pay.local/pay/TXN-");
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should return existing payment without duplicating when idempotency key already exists")
    void initiatePayment_DuplicateIdempotencyKey_ReturnsExisting() {
        Payment existing = new Payment();
        existing.setId(50L);
        existing.setOrder(order);
        existing.setPaymentMethod(PaymentMethod.CARD);
        existing.setStatus(PaymentStatus.PENDING);
        existing.setAmount(new BigDecimal("1500.00"));
        existing.setTransactionReference("TXN-EXISTING123");

        PaymentDTO.InitiatePaymentRequest request =
                new PaymentDTO.InitiatePaymentRequest(10L, PaymentMethod.CARD, "idem-key-1");

        when(paymentRepository.findByIdempotencyKey("idem-key-1")).thenReturn(Optional.of(existing));

        PaymentDTO.PaymentResponse response = paymentService.initiatePayment(1L, request);

        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.transactionReference()).isEqualTo("TXN-EXISTING123");
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).findByIdAndUserId(any(), any());
    }

    @Test
    @DisplayName("Should confirm order when webhook reports SUCCEEDED")
    void handleWebhook_Succeeded_ConfirmsOrder() {
        Payment payment = new Payment();
        payment.setId(50L);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionReference("TXN-123456");

        PaymentDTO.WebhookRequest request =
                new PaymentDTO.WebhookRequest("TXN-123456", PaymentStatus.SUCCEEDED);

        when(paymentRepository.findByTransactionReference("TXN-123456")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentDTO.PaymentResponse response = paymentService.handleWebhook(WEBHOOK_SECRET, request);

        assertThat(response.status()).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should cancel order and restore inventory when webhook reports FAILED")
    void handleWebhook_Failed_CancelsOrder() {
        Payment payment = new Payment();
        payment.setId(50L);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionReference("TXN-123456");

        PaymentDTO.WebhookRequest request =
                new PaymentDTO.WebhookRequest("TXN-123456", PaymentStatus.FAILED);

        when(paymentRepository.findByTransactionReference("TXN-123456")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentDTO.PaymentResponse response = paymentService.handleWebhook(WEBHOOK_SECRET, request);

        assertThat(response.status()).isEqualTo(PaymentStatus.FAILED);
        verify(orderService).updateOrderStatus(
                eq(10L),
                eq(new OrderDTO.UpdateOrderStatusRequest(OrderStatus.CANCELLED))
        );
    }

    @Test
    @DisplayName("Should reject webhook call when secret header is invalid")
    void handleWebhook_InvalidSecret_ThrowsAccessDenied() {
        PaymentDTO.WebhookRequest request =
                new PaymentDTO.WebhookRequest("TXN-123456", PaymentStatus.SUCCEEDED);

        assertThatThrownBy(() -> paymentService.handleWebhook("wrong-secret", request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Invalid webhook signature");

        verify(paymentRepository, never()).findByTransactionReference(any());
    }
}