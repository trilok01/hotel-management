package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.cancellation.DefaultCancellationPolicy;
import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.booking.BookingStatus;
import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.inventory.RoomInventoryManager;
import com.rupeek.hotelbooking.repository.BookingRepository;
import com.rupeek.hotelbooking.repository.PaymentRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryBookingRepository;
import com.rupeek.hotelbooking.repository.impl.InMemoryPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CancellationServiceImplTest {

    private BookingRepository bookingRepository;
    private RoomInventoryManager inventoryManager;
    private CancellationService cancellationService;

    @BeforeEach
    void setUp() {
        bookingRepository = new InMemoryBookingRepository();
        PaymentRepository paymentRepository = new InMemoryPaymentRepository();
        inventoryManager = new RoomInventoryManager();
        cancellationService = new CancellationServiceImpl(
                bookingRepository, paymentRepository, inventoryManager, new DefaultCancellationPolicy());
    }

    @Test
    void cancellingReleasesInventoryForRebooking() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(5), LocalDate.now().plusDays(6));
        Booking booking = new Booking("bk-1", "prop-1", "rt-1", "guest-1", dates, 1, 1, Money.of(1000));
        bookingRepository.save(booking);

        assertTrue(inventoryManager.tryReserve("rt-1", dates, 1, 1));
        assertFalse(inventoryManager.isAvailable("rt-1", dates, 1, 1));

        cancellationService.cancel(booking.getId());

        assertEquals(BookingStatus.CANCELLED, bookingRepository.findById(booking.getId()).get().getStatus());
        assertTrue(inventoryManager.isAvailable("rt-1", dates, 1, 1));
    }

    @Test
    void cannotCancelAnAlreadyCancelledBooking() {
        DateRange dates = new DateRange(LocalDate.now().plusDays(5), LocalDate.now().plusDays(6));
        Booking booking = new Booking("bk-2", "prop-1", "rt-1", "guest-1", dates, 1, 1, Money.of(1000));
        bookingRepository.save(booking);

        cancellationService.cancel(booking.getId());

        assertThrows(RuntimeException.class, () -> cancellationService.cancel(booking.getId()));
    }
}
