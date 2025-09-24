package com.example.bankcards.enums;

public enum Role {

    ADMIN,
    USER;

    public String getRoleWithPrefix() {
        return "ROLE_" + this.name();
    }
    public String getRoleWithoutPrefix() {
        return name();
    }
}