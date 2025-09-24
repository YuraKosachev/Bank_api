package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardStatusDto(
        @JsonProperty("card_id")
        @NotNull(message = "card_id is required")
        UUID cardId,
        @NotNull(message = "status is required")
        CardStatus status
        ) {
}
