package com.rupeek.hotelbooking.payment;

import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import com.rupeek.hotelbooking.payment.gateway.PaymentGateway;
import com.rupeek.hotelbooking.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentGatewayRegistry {

    private final Map<PaymentMethodType, PaymentGateway> gatewaysByMethod;

    public PaymentGatewayRegistry(List<PaymentGateway> gateways) {
        this.gatewaysByMethod = gateways.stream()
                .collect(Collectors.toMap(PaymentGateway::supportedMethod, Function.identity()));
    }

    public PaymentGateway resolve(PaymentMethodType method) {
        PaymentGateway gateway = gatewaysByMethod.get(method);
        if (gateway == null) {
            throw new ValidationException("Unsupported payment method: " + method);
        }
        return gateway;
    }
}
