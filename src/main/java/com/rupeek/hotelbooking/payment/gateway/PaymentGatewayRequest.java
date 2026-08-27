package com.rupeek.hotelbooking.payment.gateway;

import com.rupeek.hotelbooking.domain.common.Money;

public record PaymentGatewayRequest(String bookingId, Money amount, String payerReference) {
}
