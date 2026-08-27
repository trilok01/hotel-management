package com.rupeek.hotelbooking.cancellation;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.Money;

import java.time.LocalDateTime;

public interface CancellationPolicy {
    Money calculateRefund(Booking booking, LocalDateTime cancelledAt);
}
