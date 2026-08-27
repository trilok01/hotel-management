package com.rupeek.hotelbooking.payment.gateway;

import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpiPaymentGateway implements PaymentGateway {

    @Override
    public PaymentMethodType supportedMethod() {
        return PaymentMethodType.UPI;
    }

    @Override
    public PaymentGatewayResult charge(PaymentGatewayRequest request) {
        return PaymentGatewayResult.success("UPI-" + UUID.randomUUID());
    }
}
