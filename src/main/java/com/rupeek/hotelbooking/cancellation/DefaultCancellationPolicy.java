package com.rupeek.hotelbooking.cancellation;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.Money;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class DefaultCancellationPolicy implements CancellationPolicy {

    private static final long FULL_REFUND_HOURS = 48;
    private static final long PARTIAL_REFUND_HOURS = 24;
    private static final java.math.BigDecimal PARTIAL_REFUND_RATE = java.math.BigDecimal.valueOf(0.5);

    @Override
    public Money calculateRefund(Booking booking, LocalDateTime cancelledAt) {
        LocalDateTime checkInStart = booking.getStayDates().getCheckIn().atStartOfDay();
        long hoursToCheckIn = Duration.between(cancelledAt, checkInStart).toHours();

        if (hoursToCheckIn >= FULL_REFUND_HOURS) {
            return booking.getTotalAmount();
        } else if (hoursToCheckIn >= PARTIAL_REFUND_HOURS) {
            return booking.getTotalAmount().multiply(PARTIAL_REFUND_RATE);
        }
        return Money.ZERO;
    }
}
