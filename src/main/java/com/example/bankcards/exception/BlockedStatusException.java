package com.example.bankcards.exception;

public class BlockedStatusException extends RuntimeException {
    public BlockedStatusException(String message) {
        super(message);
    }
}
