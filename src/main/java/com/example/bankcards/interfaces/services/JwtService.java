package com.example.bankcards.interfaces.services;

import com.example.bankcards.dto.AccountDto;
import com.example.bankcards.dto.AuthorizationToken;
import com.example.bankcards.dto.ValidationResult;
import com.example.bankcards.enums.TokenStatus;
import com.example.bankcards.enums.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;
import java.util.function.Function;

public interface JwtService {
    AuthorizationToken createAuthorizationToken(AccountDto accountDto);
    AuthorizationToken refreshAuthorizationToken(String refreshToken);
    UserDetails getUserDetailsByToken(String token, TokenType type);
    <T> T extractValue(String token, TokenType type, Function<Claims,T> claimsResolver);
    void changeTokenStatus(UUID accountId, TokenStatus status);
    ValidationResult validateToken(String token, TokenType type );
}