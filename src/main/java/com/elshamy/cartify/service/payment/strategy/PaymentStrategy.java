package com.elshamy.cartify.service.payment.strategy;

import com.elshamy.cartify.model.Payment;
import com.elshamy.cartify.model.PaymentMethod;

public interface PaymentStrategy {
    boolean supports(PaymentMethod method);

    void process(Payment payment);
}