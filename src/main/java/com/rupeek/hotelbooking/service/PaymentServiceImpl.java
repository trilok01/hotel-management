package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.booking.BookingStatus;
import com.rupeek.hotelbooking.domain.payment.Payment;
import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import com.rupeek.hotelbooking.exception.NotFoundException;
import com.rupeek.hotelbooking.exception.PaymentFailedException;
import com.rupeek.hotelbooking.payment.PaymentGatewayRegistry;
import com.rupeek.hotelbooking.payment.gateway.PaymentGateway;
import com.rupeek.hotelbooking.payment.gateway.PaymentGatewayRequest;
import com.rupeek.hotelbooking.payment.gateway.PaymentGatewayResult;
import com.rupeek.hotelbooking.repository.BookingRepository;
import com.rupeek.hotelbooking.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRegistry gatewayRegistry;

    public PaymentServiceImpl(BookingRepository bookingRepository,
                               PaymentRepository paymentRepository,
                               PaymentGatewayRegistry gatewayRegistry) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.gatewayRegistry = gatewayRegistry;
    }

    @Override
    public Payment pay(String bookingId, PaymentMethodType method) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        booking.transitionTo(BookingStatus.PAYMENT_PENDING);

        Payment payment = new Payment(UUID.randomUUID().toString(), bookingId, booking.getTotalAmount(), method);

        PaymentGateway gateway = gatewayRegistry.resolve(method);
        PaymentGatewayResult result = gateway.charge(
                new PaymentGatewayRequest(bookingId, booking.getTotalAmount(), booking.getGuestId()));

        if (result.success()) {
            payment.markSuccess(result.referenceId());
            booking.transitionTo(BookingStatus.CONFIRMED);
        } else {
            payment.markFailed(result.failureReason());
            booking.transitionTo(BookingStatus.PAYMENT_FAILED);
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        if (!result.success()) {
            throw new PaymentFailedException("Payment failed for booking " + bookingId + ": " + result.failureReason());
        }
        return payment;
    }
}
