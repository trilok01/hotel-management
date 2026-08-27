package com.rupeek.hotelbooking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record BookingRequest(
        @NotBlank(message = "propertyId is required") String propertyId,
        @NotBlank(message = "roomTypeId is required") String roomTypeId,
        @NotBlank(message = "guestId is required") String guestId,
        @NotNull(message = "checkIn is required") @FutureOrPresent LocalDate checkIn,
        @NotNull(message = "checkOut is required") LocalDate checkOut,
        @Positive(message = "numGuests must be positive") int numGuests,
        @Positive(message = "numRooms must be positive") int numRooms) {
}
