package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AccountCreateDto(
        @NotNull(message = "login is required")
        String login,

        @NotNull (message = "lastname is required")
        @JsonProperty("last_name")
        String lastName,

        @NotNull(message ="firstname is requerd")
        @JsonProperty("first_name")
        String firstName,

        @NotNull(message ="email is requerd")
        @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @NotEmpty(message = "Email cannot be empty")
        String email,

        @NotNull(message = "password is required")
        String password,

        @NotNull(message = "confiramation password is required")
        String passwordConfirm
) { }
