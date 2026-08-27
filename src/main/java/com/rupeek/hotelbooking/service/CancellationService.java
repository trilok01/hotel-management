package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.booking.Booking;

public interface CancellationService {

    Booking cancel(String bookingId);
}
