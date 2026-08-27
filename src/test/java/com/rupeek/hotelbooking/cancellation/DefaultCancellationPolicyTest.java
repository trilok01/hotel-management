package com.rupeek.hotelbooking.cancellation;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultCancellationPolicyTest {

    private final DefaultCancellationPolicy policy = new DefaultCancellationPolicy();

    private Booking bookingCheckingInOn(LocalDate checkIn) {
        DateRange dates = new DateRange(checkIn, checkIn.plusDays(2));
        return new Booking("bk-1", "prop-1", "rt-1", "guest-1", dates, 2, 1, Money.of(4000));
    }

    @Test
    void fullRefundWhenCancelledMoreThan48HoursBeforeCheckIn() {
        Booking booking = bookingCheckingInOn(LocalDate.now().plusDays(5));
        Money refund = policy.calculateRefund(booking, LocalDateTime.now());
        assertEquals(Money.of(4000), refund);
    }

    @Test
    void halfRefundWhenCancelledBetween24And48HoursBeforeCheckIn() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        Booking booking = bookingCheckingInOn(checkIn);
        LocalDateTime cancelledAt = checkIn.atStartOfDay().minusHours(30);
        Money refund = policy.calculateRefund(booking, cancelledAt);
        assertEquals(Money.of(2000), refund);
    }

    @Test
    void noRefundWhenCancelledLessThan24HoursBeforeCheckIn() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        Booking booking = bookingCheckingInOn(checkIn);
        LocalDateTime cancelledAt = checkIn.atStartOfDay().minusHours(2);
        Money refund = policy.calculateRefund(booking, cancelledAt);
        assertEquals(Money.ZERO, refund);
    }
}
