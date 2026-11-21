package com.elshamy.cartify.service.payment.strategy;

import com.elshamy.cartify.model.OrderStatus;
import com.elshamy.cartify.model.Payment;
import com.elshamy.cartify.model.PaymentMethod;
import com.elshamy.cartify.model.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryStrategy implements PaymentStrategy {

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.CASH_ON_DELIVERY;
    }

    @Override
    public void process(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.getOrder().setStatus(OrderStatus.CONFIRMED);
    }
}