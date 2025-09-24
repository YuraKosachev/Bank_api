package com.example.bankcards.util;

public final class CardUtils {
    public static String maskCardNumber(String cardNumber) {
        if(cardNumber == null || cardNumber.startsWith("**** **** ****")) return cardNumber;
        return "**** **** **** " + cardNumber.substring(12);
    }
}
