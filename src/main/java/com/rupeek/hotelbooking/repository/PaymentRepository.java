package com.rupeek.hotelbooking.repository;

import com.rupeek.hotelbooking.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    List<Payment> findByBookingId(String bookingId);
}
