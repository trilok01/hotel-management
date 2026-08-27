package com.rupeek.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RoomTypeRequest(
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "pricePerNight is required") @Positive BigDecimal pricePerNight,
        @Positive(message = "maxGuestsPerRoom must be positive") int maxGuestsPerRoom,
        @Positive(message = "totalRooms must be positive") int totalRooms) {
}
