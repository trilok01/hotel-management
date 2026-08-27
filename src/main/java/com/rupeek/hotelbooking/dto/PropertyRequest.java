package com.rupeek.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PropertyRequest(
        @NotBlank(message = "ownerId is required") String ownerId,
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "city is required") String city,
        String locality,
        String address,
        Set<String> amenities,
        @NotNull(message = "starRating is required") Double starRating) {
}
