package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardCreateDto(
        @Pattern(
                regexp = "^\\d{16}$",
                message = "Card number must contain exactly 16 digits with no spaces"
        )
        @NotNull(message = "Card number is required")
        String number,
        @PositiveOrZero(message = "card balance must be greater than 0 or equals")
        @NotNull(message = "card balance is required")
        @JsonProperty("balance")
        BigDecimal balance,
        @NotNull(message = "expired_in is required")
        @JsonProperty("expired_in")
        LocalDate expiredIn,

        @NotNull(message = "account is required")
        @JsonProperty("account_id")
        UUID accountId) {

        public CardCreateDto{
                if(balance == null) balance = BigDecimal.ZERO;
        }
}
