package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardBlockedRequestDto(
        @NotNull(message = "target_id is required")
        @JsonProperty("target_id")
        UUID targetId) {
}
