package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.booking.BookingStatus;
import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.payment.Payment;
import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import com.rupeek.hotelbooking.domain.payment.PaymentStatus;
import com.rupeek.hotelbooking.exception.PaymentFailedException;
import com.rupeek.hotelbooking.payment.PaymentGatewayRegistry;
import com.rupeek.hotelbooking.payment.gateway.CardPaymentGateway;
import com.rupeek.hotelbooking.payment.gateway.WalletPaymentGateway;
import com.rupeek.hotelbooking.repository.BookingRepository;
import com.rupeek.hotelbooking.repository.PaymentRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryBookingRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceImplTest {

    private BookingRepository bookingRepository;
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository = new InMemoryBookingRepository();
        paymentRepository = new InMemoryPaymentRepository();
        PaymentGatewayRegistry registry = new PaymentGatewayRegistry(
                List.of(new CardPaymentGateway(), new WalletPaymentGateway()));
        paymentService = new PaymentServiceImpl(bookingRepository, paymentRepository, registry);

        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        booking = new Booking("bk-1", "prop-1", "rt-1", "guest-1", dates, 2, 1, Money.of(3000));
        bookingRepository.save(booking);
    }

    @Test
    void successfulCardPaymentConfirmsBooking() {
        Payment payment = paymentService.pay(booking.getId(), PaymentMethodType.CARD);

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(BookingStatus.CONFIRMED, bookingRepository.findById(booking.getId()).get().getStatus());
    }

    @Test
    void failedPaymentMarksBookingAsPaymentFailedButKeepsInventoryHeld() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Booking expensiveBooking = new Booking("bk-2", "prop-1", "rt-1", "guest-2", dates, 2, 1, Money.of(60000));
        bookingRepository.save(expensiveBooking);

        assertThrows(PaymentFailedException.class,
                () -> paymentService.pay(expensiveBooking.getId(), PaymentMethodType.WALLET));

        assertEquals(BookingStatus.PAYMENT_FAILED,
                bookingRepository.findById(expensiveBooking.getId()).get().getStatus());
        assertTrue(bookingRepository.findById(expensiveBooking.getId()).get().getStatus().isCancellable());
    }

    @Test
    void cannotPayForAnAlreadyConfirmedBooking() {
        paymentService.pay(booking.getId(), PaymentMethodType.CARD);

        assertThrows(RuntimeException.class, () -> paymentService.pay(booking.getId(), PaymentMethodType.CARD));
    }
}
