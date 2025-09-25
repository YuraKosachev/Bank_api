package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDto(
        @NotNull(message = "card_id is required")
        @JsonProperty("card_id")
        UUID cardId,

        @Positive(message = "sum must be greater than 0")
        @NotNull(message = "sum is required")
        @JsonProperty("sum")
        BigDecimal sum

) {
}
