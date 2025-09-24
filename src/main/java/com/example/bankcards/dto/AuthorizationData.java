package com.example.bankcards.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthorizationData(
        @NotNull(message = "login is required")
        @NotEmpty(message = "login is required")
        String login,

        @NotNull(message = "password is required")
        @NotEmpty(message = "password is required")
        String password){}
