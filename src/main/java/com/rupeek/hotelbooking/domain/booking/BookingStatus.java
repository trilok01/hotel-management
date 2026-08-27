package com.rupeek.hotelbooking.domain.booking;

import java.util.EnumSet;
import java.util.Set;

public enum BookingStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    CONFIRMED,
    CANCELLED,
    COMPLETED;

    private static final Set<BookingStatus> CANCELLABLE = EnumSet.of(CREATED, PAYMENT_PENDING, PAYMENT_FAILED, CONFIRMED);

    public boolean canTransitionTo(BookingStatus target) {
        return switch (this) {
            case CREATED -> target == PAYMENT_PENDING || target == CANCELLED;
            case PAYMENT_PENDING -> target == CONFIRMED || target == PAYMENT_FAILED || target == CANCELLED;
            case PAYMENT_FAILED -> target == PAYMENT_PENDING || target == CANCELLED;
            case CONFIRMED -> target == CANCELLED || target == COMPLETED;
            case CANCELLED, COMPLETED -> false;
        };
    }

    public boolean isCancellable() {
        return CANCELLABLE.contains(this);
    }

    public boolean holdsInventory() {
        return this == CREATED || this == PAYMENT_PENDING || this == PAYMENT_FAILED || this == CONFIRMED;
    }
}
