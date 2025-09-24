package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CardTransferDto(
        @JsonProperty("source_id")
        @NotNull(message = "source_id is required")
        UUID sourceId,

        @JsonProperty("target_id")
        @NotNull(message = "target_id is required")
        UUID targetId,

        @Positive(message = "Transfer amount must be greater than 0")
        @NotNull(message = "Transfer amount is required")
        @JsonProperty("sum")
        BigDecimal sum
) {
}
