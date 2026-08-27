package com.rupeek.hotelbooking.controller;

import com.rupeek.hotelbooking.domain.payment.Payment;
import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;
import com.rupeek.hotelbooking.dto.PaymentRequest;
import com.rupeek.hotelbooking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings/{bookingId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment pay(@PathVariable String bookingId, @Valid @RequestBody PaymentRequest request) {
        return paymentService.pay(bookingId, PaymentMethodType.of(request.method()));
    }
}
