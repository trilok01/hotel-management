package com.rupeek.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;

public record OwnerRequest(
        @NotBlank(message = "name is required") String name,
        String contactEmail) {
}
