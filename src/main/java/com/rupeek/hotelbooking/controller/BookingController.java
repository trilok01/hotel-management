package com.rupeek.hotelbooking.controller;

import com.rupeek.hotelbooking.domain.booking.Booking;
import com.rupeek.hotelbooking.domain.common.DateRange;
import com.rupeek.hotelbooking.dto.BookingRequest;
import com.rupeek.hotelbooking.service.BookingService;
import com.rupeek.hotelbooking.service.CancellationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CancellationService cancellationService;

    public BookingController(BookingService bookingService, CancellationService cancellationService) {
        this.bookingService = bookingService;
        this.cancellationService = cancellationService;
    }

    @PostMapping
    public ResponseEntity<Booking> book(@Valid @RequestBody BookingRequest request) {
        DateRange dates = new DateRange(request.checkIn(), request.checkOut());
        Booking booking = bookingService.book(request.propertyId(), request.roomTypeId(), request.guestId(),
                dates, request.numGuests(), request.numRooms());
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/{bookingId}")
    public Booking get(@PathVariable String bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public Booking cancel(@PathVariable String bookingId) {
        return cancellationService.cancel(bookingId);
    }
}
