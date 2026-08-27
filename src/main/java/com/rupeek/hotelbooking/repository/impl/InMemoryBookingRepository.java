package com.rupeek.hotelbooking.repository.impl;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.repository.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> store = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        store.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Booking> findByRoomTypeId(String roomTypeId) {
        return store.values().stream()
                .filter(b -> b.getRoomTypeId().equals(roomTypeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByGuestId(String guestId) {
        return store.values().stream()
                .filter(b -> b.getGuestId().equals(guestId))
                .collect(Collectors.toList());
    }
}
