package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.DateRange;

public interface BookingService {

    Booking book(String propertyId, String roomTypeId, String guestId, DateRange dates, int numGuests, int numRooms);

    Booking getBooking(String bookingId);
}
