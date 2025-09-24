package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

public record CardCreateDto(
        @Pattern(
                regexp = "^\\d{16}$",
                message = "Card number must contain exactly 16 digits with no spaces"
        )
        @NotNull(message = "Card number is required")
        String number,

        @NotNull(message = "expired_in is required")
        @JsonProperty("expired_in")
        LocalDate expiredIn,

        @NotNull(message = "account is required")
        @JsonProperty("account_id")
        UUID accountId) {
}
