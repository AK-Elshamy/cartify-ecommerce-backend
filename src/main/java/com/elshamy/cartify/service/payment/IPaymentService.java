package com.elshamy.cartify.service.payment;

import com.elshamy.cartify.dtos.PaymentDTO;

import java.util.List;

public interface IPaymentService {
    PaymentDTO.PaymentResponse initiatePayment(Long userId, PaymentDTO.InitiatePaymentRequest request);

    PaymentDTO.PaymentResponse handleWebhook(String webhookSecret, PaymentDTO.WebhookRequest request);

    List<PaymentDTO.PaymentResponse> getOrderPayments(Long userId, Long orderId);
}