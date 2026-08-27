package com.rupeek.hotelbooking.domain.payment;

import com.rupeek.hotelbooking.domain.common.Money;

import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {

    private final String id;
    private final String bookingId;
    private final Money amount;
    private final PaymentMethodType method;
    private PaymentStatus status;
    private String gatewayReference;
    private final LocalDateTime createdAt;

    public Payment(String id, String bookingId, Money amount, PaymentMethodType method) {
        this.id = Objects.requireNonNull(id);
        this.bookingId = Objects.requireNonNull(bookingId);
        this.amount = Objects.requireNonNull(amount);
        this.method = Objects.requireNonNull(method);
        this.status = PaymentStatus.INITIATED;
        this.createdAt = LocalDateTime.now();
    }

    public void markSuccess(String gatewayReference) {
        this.status = PaymentStatus.SUCCESS;
        this.gatewayReference = gatewayReference;
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.gatewayReference = reason;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public String getId() {
        return id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentMethodType getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getGatewayReference() {
        return gatewayReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
