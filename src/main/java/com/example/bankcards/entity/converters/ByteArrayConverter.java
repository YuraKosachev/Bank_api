package com.example.bankcards.entity.converters;

import com.example.bankcards.service.EncryptionService;
import com.example.bankcards.util.CardUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

@Converter
@RequiredArgsConstructor
public class ByteArrayConverter implements AttributeConverter<String,byte[]>{

    private final EncryptionService encryptionService;

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        return encryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        return CardUtils.maskCardNumber(encryptionService.decrypt(dbData));
    }

}