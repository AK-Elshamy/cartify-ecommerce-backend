package com.elshamy.cartify.controller;

import com.elshamy.cartify.dtos.PaymentDTO;
import com.elshamy.cartify.model.User;
import com.elshamy.cartify.service.payment.IPaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentDTO.PaymentResponse> initiatePayment(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody PaymentDTO.InitiatePaymentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(currentUser.getId(), request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<PaymentDTO.PaymentResponse> handleWebhook(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String webhookSecret,
            @Valid @RequestBody PaymentDTO.WebhookRequest request
    ) {
        return ResponseEntity.ok(paymentService.handleWebhook(webhookSecret, request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO.PaymentResponse>> getOrderPayments(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @Positive Long orderId
    ) {
        return ResponseEntity.ok(paymentService.getOrderPayments(currentUser.getId(), orderId));
    }
}