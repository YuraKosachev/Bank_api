package com.example.bankcards.interfaces.services;

import com.example.bankcards.dto.CardBlockedRequestDto;
import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardTransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.interfaces.services.base.Creatable;
import com.example.bankcards.interfaces.services.base.Deletable;
import com.example.bankcards.interfaces.services.base.Pagetable;
import com.example.bankcards.interfaces.services.base.Searchable;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;

public interface CardService
        extends Deletable,
        Creatable<CardCreateDto, Card>,
        Pagetable<Card>
{
    void changeCardStatus(UUID cardId, CardStatus newStatus);
    void transfer(UUID accountId, CardTransferDto transferDto);
    void setBlockRequest(UUID accountId, CardBlockedRequestDto blockRequest);
    <T> T findById(UUID accountId, UUID id, Function<Card,T> mapper);
}
