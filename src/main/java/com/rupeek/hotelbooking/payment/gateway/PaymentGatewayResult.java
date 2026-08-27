package com.rupeek.hotelbooking.payment.gateway;

public record PaymentGatewayResult(boolean success, String referenceId, String failureReason) {

    public static PaymentGatewayResult success(String referenceId) {
        return new PaymentGatewayResult(true, referenceId, null);
    }

    public static PaymentGatewayResult failure(String reason) {
        return new PaymentGatewayResult(false, null, reason);
    }
}
