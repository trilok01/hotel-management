package com.rupeek.hotelbooking.domain;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.booking.BookingStatus;
import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.exception.InvalidBookingStateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateMachineTest {

    private Booking newBooking() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        return new Booking("bk-1", "prop-1", "rt-1", "guest-1", dates, 1, 1, Money.of(1000));
    }

    @Test
    void happyPathTransitions() {
        Booking booking = newBooking();
        booking.transitionTo(BookingStatus.PAYMENT_PENDING);
        booking.transitionTo(BookingStatus.CONFIRMED);
        booking.transitionTo(BookingStatus.COMPLETED);
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
    }

    @Test
    void cannotConfirmDirectlyFromCreated() {
        Booking booking = newBooking();
        assertThrows(InvalidBookingStateException.class, () -> booking.transitionTo(BookingStatus.CONFIRMED));
    }

    @Test
    void cannotReviveACancelledBooking() {
        Booking booking = newBooking();
        booking.transitionTo(BookingStatus.CANCELLED);
        assertThrows(InvalidBookingStateException.class, () -> booking.transitionTo(BookingStatus.PAYMENT_PENDING));
    }

    @Test
    void failedPaymentCanBeRetried() {
        Booking booking = newBooking();
        booking.transitionTo(BookingStatus.PAYMENT_PENDING);
        booking.transitionTo(BookingStatus.PAYMENT_FAILED);
        assertDoesNotThrow(() -> booking.transitionTo(BookingStatus.PAYMENT_PENDING));
    }
}
