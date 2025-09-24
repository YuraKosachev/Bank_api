package com.example.bankcards.controller;

import com.example.bankcards.constants.ApiConstants;
import com.example.bankcards.constants.SecurityConstants;
import com.example.bankcards.dto.AuthorizationData;
import com.example.bankcards.dto.AuthorizationToken;
import com.example.bankcards.dto.ErrorMessage;
import com.example.bankcards.dto.RefreshData;
import com.example.bankcards.entity.Account;
import com.example.bankcards.enums.TokenStatus;
import com.example.bankcards.interfaces.services.AccountService;
import com.example.bankcards.interfaces.services.JwtService;
import com.example.bankcards.mappers.AccountMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.API_PREFIX_V1)
@Tag(name = ApiConstants.Authorization.API_AUTHORIZATION_CONTROLLER_NAME)
@RequiredArgsConstructor
public class AuthorizationController {
    private final AccountService accountService;
    private final JwtService jwtService;


    @PostMapping(ApiConstants.Authorization.API_AUTHORIZATION_LOGIN)
    @Operation(description = "Endpoint to login",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthorizationToken.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    public ResponseEntity<AuthorizationToken> login(@RequestBody @Valid AuthorizationData request) {
        var accountDto = accountService.getAccountBy(request);
        return ResponseEntity.ok(jwtService.createAuthorizationToken(accountDto));
    }

    @PostMapping(ApiConstants.Authorization.API_AUTHORIZATION_REFRESH)
    @Operation(description = "Endpoint to refresh token",
            summary = "This is a summary for account post endpoint")
    @ApiResponse(description = "Success", responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthorizationToken.class))})
    @ApiResponse(description = "Error", responseCode = "400",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))})
    @ResponseBody
    public ResponseEntity<AuthorizationToken> refresh(@RequestBody @Valid RefreshData request) {
        return ResponseEntity.ok(jwtService.refreshAuthorizationToken(request.refreshToken()));
    }

    @GetMapping(ApiConstants.Authorization.API_AUTHORIZATION_LOGOUT)
    @Operation(description = "Endpoint to logout",
            summary = "This is a summary for account post endpoint")
    @SecurityRequirement(name = SecurityConstants.AUTH_BEARER_TOKEN)
    public ResponseEntity<?> logout(UsernamePasswordAuthenticationToken token) {
        jwtService.changeTokenStatus(((Account)token.getPrincipal()).getId(), TokenStatus.INACTIVE);
        return ResponseEntity.noContent().build();
    }
}
