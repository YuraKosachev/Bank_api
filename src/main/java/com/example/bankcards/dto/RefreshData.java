package com.example.bankcards.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RefreshData(
        @NotNull(message = "token is required")
        @NotEmpty(message = "token is required")
        String refreshToken) { }