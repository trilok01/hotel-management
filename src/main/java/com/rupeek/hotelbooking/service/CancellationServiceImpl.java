package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.cancellation.CancellationPolicy;
import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.Money;
import com.rupeek.hotelbooking.domain.payment.Payment;
import com.rupeek.hotelbooking.domain.payment.PaymentStatus;
import com.rupeek.hotelbooking.exception.InvalidBookingStateException;
import com.rupeek.hotelbooking.exception.NotFoundException;
import com.rupeek.hotelbooking.inventory.RoomInventoryManager;
import com.rupeek.hotelbooking.repository.BookingRepository;
import com.rupeek.hotelbooking.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CancellationServiceImpl implements CancellationService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomInventoryManager inventoryManager;
    private final CancellationPolicy cancellationPolicy;

    public CancellationServiceImpl(BookingRepository bookingRepository,
                                    PaymentRepository paymentRepository,
                                    RoomInventoryManager inventoryManager,
                                    CancellationPolicy cancellationPolicy) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.inventoryManager = inventoryManager;
        this.cancellationPolicy = cancellationPolicy;
    }

    @Override
    public Booking cancel(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!booking.getStatus().isCancellable()) {
            throw new InvalidBookingStateException(
                    "Booking " + bookingId + " cannot be cancelled from state " + booking.getStatus());
        }

        Money refund = cancellationPolicy.calculateRefund(booking, LocalDateTime.now());
        booking.markCancelled(refund);

        inventoryManager.release(booking.getRoomTypeId(), booking.getStayDates(), booking.getNumRooms());

        if (refund.getAmount().signum() > 0) {
            List<Payment> payments = paymentRepository.findByBookingId(bookingId);
            payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .findFirst()
                    .ifPresent(p -> {
                        p.markRefunded();
                        paymentRepository.save(p);
                    });
        }

        return bookingRepository.save(booking);
    }
}
