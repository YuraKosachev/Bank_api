package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RoleUpdateDto(
        @NotNull(message = "id is required")
        @JsonProperty("account_id")
        UUID accountId,

        @NotNull (message = "lastname is required")
        Role role
) {
}