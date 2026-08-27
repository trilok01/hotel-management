package com.rupeek.hotelbooking.repository;

import com.rupeek.hotelbooking.domain.booking.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findByRoomTypeId(String roomTypeId);
    List<Booking> findByGuestId(String guestId);
}
