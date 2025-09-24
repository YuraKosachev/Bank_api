package com.example.bankcards.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${security.crypto-key}")
    private String encryptionKey;

    public byte[] encrypt(String plaintext) {
        if (plaintext == null) return null;

        return jdbcTemplate.queryForObject(
                "SELECT pgp_sym_encrypt(?, ?)",
                new Object[]{plaintext, encryptionKey},
                byte[].class
        );
    }

    public String decrypt(byte[] ciphertext) {
        if (ciphertext == null) return null;

        return jdbcTemplate.queryForObject(
                "SELECT pgp_sym_decrypt(?, ?)",
                new Object[]{ciphertext, encryptionKey},
                String.class
        );
    }
}