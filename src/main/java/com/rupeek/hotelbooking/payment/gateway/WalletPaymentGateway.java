package com.rupeek.hotelbooking.payment.gateway;

import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WalletPaymentGateway implements PaymentGateway {

    private static final BigDecimal WALLET_LIMIT = BigDecimal.valueOf(50_000);

    @Override
    public PaymentMethodType supportedMethod() {
        return PaymentMethodType.WALLET;
    }

    @Override
    public PaymentGatewayResult charge(PaymentGatewayRequest request) {
        if (request.amount().getAmount().compareTo(WALLET_LIMIT) > 0) {
            return PaymentGatewayResult.failure("Wallet balance limit exceeded");
        }
        return PaymentGatewayResult.success("WALLET-" + UUID.randomUUID());
    }
}
