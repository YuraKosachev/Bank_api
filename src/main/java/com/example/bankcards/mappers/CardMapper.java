package com.example.bankcards.mappers;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardUtils;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public Card toEntity(CardCreateDto dto) {
        if(dto == null) return null;
        return Card.builder()
                .cardNumber(dto.number())
                .account(Account.builder().id(dto.accountId()).build())
                .expiredIn(dto.expiredIn())
                .balance(dto.balance())
                .build();
    }

    public CardDto toDto(Card entity) {
        if(entity == null) return null;
        return CardDto.builder()
                .id(entity.getId())
                .owner(entity.getOwner())
                .expiryDate(entity.getExpiredIn())
                .number(CardUtils.maskCardNumber(entity.getCardNumber()))
                .status(entity.getStatus())
                .balance(entity.getBalance())
                .build();
    }
}
