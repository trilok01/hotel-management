package com.rupeek.hotelbooking.payment.gateway;

import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;

public interface PaymentGateway {

    PaymentMethodType supportedMethod();

    PaymentGatewayResult charge(PaymentGatewayRequest request);
}
