package com.rupeek.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(@NotBlank(message = "method is required") String method) {
}
