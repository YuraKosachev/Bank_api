package com.example.bankcards.mappers;

import com.example.bankcards.dto.AuthorizationToken;
import com.example.bankcards.dto.TokenDto;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Token;
import com.example.bankcards.enums.TokenStatus;
import org.springframework.stereotype.Component;

@Component
public final class TokenMapper {

    public Token dtoToEntity(TokenDto accessDto, TokenDto refreshDto, Account account, TokenStatus status) {
        var builder = Token.builder();

        builder.accessToken(accessDto.getToken())
                .refreshToken(refreshDto.getToken())
                .refreshTokenExpires(refreshDto.getExpires())
                .account(account)
                .status(status)
                .accessTokenExpires(accessDto.getExpires());

        return builder.build();
    }

    public AuthorizationToken entityToDto(Token entity) {
        return new AuthorizationToken(entity.getAccessToken(), entity.getRefreshToken(), entity.getAccessTokenExpires());
    }
}
