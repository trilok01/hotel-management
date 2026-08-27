package com.rupeek.hotelbooking.payment.gateway;

import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardPaymentGateway implements PaymentGateway {

    @Override
    public PaymentMethodType supportedMethod() {
        return PaymentMethodType.CARD;
    }

    @Override
    public PaymentGatewayResult charge(PaymentGatewayRequest request) {
        return PaymentGatewayResult.success("CARD-" + UUID.randomUUID());
    }
}
