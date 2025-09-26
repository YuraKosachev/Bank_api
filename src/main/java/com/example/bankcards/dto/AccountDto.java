package com.example.bankcards.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    UUID id;
    String login;
    String firstName;
    String lastName;
    String email;
    String role;
}