package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.AccessException;
import com.example.bankcards.exception.ElementNotFoundException;
import com.example.bankcards.interfaces.services.CardService;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;

    @Override
    public <T> T create(CardCreateDto dto, Function<Card, T> mapper) {
        var card = accountRepository.getActiveAccountById(dto.accountId())
                .map((acc) -> {
                    var entity = cardMapper.toEntity(dto);
                    entity.setAccount(acc);
                    return entity;
                })
                .orElseThrow(() -> new UsernameNotFoundException("Username not found or blocked"));

        return mapper.apply(cardRepository.save(card));
    }

    @Override
    public void deleteById(UUID id) {
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Page<T> getPages(Specification<Card> specification, Pageable pageable, Function<Card, T> mapper) {
        return cardRepository.findAll(specification, pageable).map(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T findById(UUID accountId, UUID id, Function<Card, T> mapper) {
        return cardRepository.findByAccountId(accountId, id).map(mapper).orElseThrow(()-> new ElementNotFoundException("Card not found"));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void changeCardStatus(UUID cardId, CardStatus newStatus) {
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ElementNotFoundException("card not found"));

        if(newStatus == CardStatus.BLOCKED && card.getBlockedRequestAt() != null) {
            card.setBlockedRequestAt(null);
        }

        card.setStatus(newStatus);
        cardRepository.save(card);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setBlockRequest(UUID accountId, CardBlockedRequestDto blockRequest){
        var card = cardRepository.findById(blockRequest.targetId())
                .orElseThrow(() -> new ElementNotFoundException("card not found"));

        if(!card.getAccount().getId().equals(accountId)) {
            throw new AccessException("You don't have access to this card");
        }

        if(card.getStatus() == CardStatus.BLOCKED || card.getStatus() == CardStatus.EXPIRED) {
            throw new ElementNotFoundException("card is blocked or expiried");
        }

        if(card.getBlockedRequestAt() != null) {
            throw new ElementNotFoundException("The request already sent");
        }

        card.setBlockedRequestAt(LocalDateTime.now());
        cardRepository.save(card);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(UUID accountId, CardTransferDto transferDto) {

        var cards = cardRepository.findAllById(Set.of(transferDto.sourceId(),transferDto.targetId()));
        if(cards.isEmpty()) {
            throw new ElementNotFoundException("Cards not found");
        }

        //get source card from collection by predicate -> if not found throw exception
        var sourceCard = cards.stream().filter(c -> c.getId().equals(transferDto.sourceId())
                        //only for active card
                        && c.getStatus() == CardStatus.ACTIVE
                        //check account -> allowed transfer between account's cards
                        && c.getAccount().getId().equals(accountId)
                        && (c.getBalance().subtract(transferDto.sum())).compareTo(BigDecimal.ZERO) >= 0)
                .findFirst()
                .orElseThrow(() -> new ElementNotFoundException("Source card not found, blocked/expired or not enough money"));

        //get target card from collection by predicate -> if not found throw exception
        var targetCard = cards.stream().filter(c->c.getId().equals(transferDto.targetId())
                        //check account -> allowed transfer between account's cards
                        && c.getAccount().getId().equals(accountId)
                        //only for active card
                        && c.getStatus() == CardStatus.ACTIVE)
                .findFirst()
                .orElseThrow(()->new ElementNotFoundException("Target card not found, blocked/expired"));

        sourceCard.setBalance(sourceCard.getBalance().subtract(transferDto.sum()));
        targetCard.setBalance(targetCard.getBalance().add(transferDto.sum()));

        cardRepository.saveAll(List.of(sourceCard,targetCard));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void balance(UUID accountId, BalanceDto balanceDto, boolean isAdmin) {
        var card = cardRepository.findActiveById(balanceDto.cardId())
                .orElseThrow(()->new ElementNotFoundException("card not found or blocked/expired"));
        if(!card.getAccount().getId().equals(accountId) && !isAdmin) {
            throw new AccessException("You don't have access to this card");
        }
        card.setBalance(card.getBalance().add(balanceDto.sum()));
        cardRepository.save(card);
    }


}