package com.elshamy.cartify.service.payment.strategy;

import com.elshamy.cartify.model.Payment;
import com.elshamy.cartify.model.PaymentMethod;
import com.elshamy.cartify.model.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.CARD;
    }

    @Override
    public void process(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentUrl("https://checkout.cartify-pay.local/pay/" + payment.getTransactionReference());
    }
}