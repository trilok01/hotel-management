package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.payment.Payment;
import com.rupeek.hotelbooking.domain.payment.PaymentMethodType;

public interface PaymentService {

    Payment pay(String bookingId, PaymentMethodType method);
}
