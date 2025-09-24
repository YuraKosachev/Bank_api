package com.example.bankcards.repository;

import com.example.bankcards.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query("select t from Token t where t.account.id = :accountId")
    List<Token> getTokensByAccountId(UUID accountId);

    @Query("select t from Token t where t.status = 0 and t.refreshToken = :refreshToken")
    Optional<Token> findTokenByRefreshToken(String refreshToken);

    @Query("select t from Token t where t.status = 0 and t.accessToken = :accessToken")
    Optional<Token> findTokenByAccessToken(String accessToken);
}
