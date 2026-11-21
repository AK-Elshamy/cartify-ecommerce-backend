package com.elshamy.cartify.service.payment;

import com.elshamy.cartify.dtos.OrderDTO;
import com.elshamy.cartify.dtos.PaymentDTO;
import com.elshamy.cartify.exception.ResourceNotFoundException;
import com.elshamy.cartify.model.Order;
import com.elshamy.cartify.model.OrderStatus;
import com.elshamy.cartify.model.Payment;
import com.elshamy.cartify.model.PaymentStatus;
import com.elshamy.cartify.repository.OrderRepository;
import com.elshamy.cartify.repository.PaymentRepository;
import com.elshamy.cartify.service.order.IOrderService;
import com.elshamy.cartify.service.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final IOrderService orderService;
    private final List<PaymentStrategy> paymentStrategies;

    @Value("${application.payment.webhook-secret}")
    private String expectedWebhookSecret;

    @Override
    @Transactional
    public PaymentDTO.PaymentResponse initiatePayment(Long userId, PaymentDTO.InitiatePaymentRequest request) {

        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(request.idempotencyKey());
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            if (!payment.getOrder().getUser().getId().equals(userId)) {
                throw new AccessDeniedException("Idempotency key belongs to another user");
            }
            return PaymentDTO.PaymentResponse.from(payment);
        }


        Order order = orderRepository.findByIdAndUserId(request.orderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.orderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Payment can only be initiated for PENDING orders. Current status: " + order.getStatus()
            );
        }


        PaymentStrategy strategy = paymentStrategies.stream()
                .filter(s -> s.supports(request.paymentMethod()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported payment method: " + request.paymentMethod()
                ));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setIdempotencyKey(request.idempotencyKey());
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        strategy.process(payment);

        orderRepository.save(order);
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentDTO.PaymentResponse.from(savedPayment);
    }

    @Override
    @Transactional
    public PaymentDTO.PaymentResponse handleWebhook(String webhookSecret, PaymentDTO.WebhookRequest request) {

        if (webhookSecret == null || !webhookSecret.equals(expectedWebhookSecret)) {
            throw new AccessDeniedException("Invalid webhook signature");
        }

        Payment payment = paymentRepository.findByTransactionReference(request.transactionReference())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with transaction reference: " + request.transactionReference()
                ));


        if (payment.getStatus() != PaymentStatus.PENDING) {
            return PaymentDTO.PaymentResponse.from(payment);
        }

        Order order = payment.getOrder();

        if (request.status() == PaymentStatus.SUCCEEDED) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        } else if (request.status() == PaymentStatus.FAILED) {
            payment.setStatus(PaymentStatus.FAILED);
            orderService.updateOrderStatus(
                    order.getId(),
                    new OrderDTO.UpdateOrderStatusRequest(OrderStatus.CANCELLED)
            );
        } else {
            throw new IllegalArgumentException("Unsupported webhook status: " + request.status());
        }

        return PaymentDTO.PaymentResponse.from(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO.PaymentResponse> getOrderPayments(Long userId, Long orderId) {
        return paymentRepository.findByOrderIdAndOrderUserId(orderId, userId).stream()
                .map(PaymentDTO.PaymentResponse::from)
                .toList();
    }
}