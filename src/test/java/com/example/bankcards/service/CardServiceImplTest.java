package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Account;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.AccessException;
import com.example.bankcards.exception.ElementNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private UUID accountId;
    private UUID cardId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        cardId = UUID.randomUUID();
    }

    @Test
    void testCreateCard_Success() {
        CardCreateDto dto = new CardCreateDto("1234567812345678", BigDecimal.TEN, LocalDate.now(), accountId);
        Account account = new Account();
        Card card = new Card();

        when(accountRepository.getActiveAccountById(accountId)).thenReturn(Optional.of(account));
        when(cardMapper.toEntity(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);

        var result = cardService.create(dto, c -> c);
        assertNotNull(result);
        verify(cardRepository).save(card);
    }

    @Test
    void testCreateCard_AccountNotFound() {
        CardCreateDto dto = new CardCreateDto("1234567812345678", BigDecimal.TEN, LocalDate.now(), accountId);
        when(accountRepository.getActiveAccountById(accountId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cardService.create(dto, c -> c));
    }

    @Test
    void testBalance_Increase_Success() {
        Card card = new Card();
        card.setId(cardId);
        card.setAccount(new Account());
        card.getAccount().setId(accountId);
        card.setBalance(BigDecimal.ZERO);

        BalanceDto dto = new BalanceDto(cardId, BigDecimal.valueOf(100));

        when(cardRepository.findActiveById(cardId)).thenReturn(Optional.of(card));
        cardService.balance(accountId, dto, false);

        assertEquals(BigDecimal.valueOf(100), card.getBalance());
        verify(cardRepository).save(card);
    }

    @Test
    void testBalance_AccessDenied() {
        Card card = new Card();
        card.setId(cardId);
        card.setAccount(new Account());
        card.getAccount().setId(UUID.randomUUID());

        BalanceDto dto = new BalanceDto(cardId, BigDecimal.valueOf(100));
        when(cardRepository.findActiveById(cardId)).thenReturn(Optional.of(card));

        assertThrows(AccessException.class, () -> cardService.balance(accountId, dto, false));
    }

    @Test
    void testTransfer_Success() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        Card source = new Card();
        source.setId(sourceId);
        source.setStatus(CardStatus.ACTIVE);
        source.setBalance(BigDecimal.valueOf(100));
        Account acc = new Account(); acc.setId(accountId);
        source.setAccount(acc);

        Card target = new Card();
        target.setId(targetId);
        target.setStatus(CardStatus.ACTIVE);
        target.setBalance(BigDecimal.ZERO);
        target.setAccount(acc);

        CardTransferDto dto = new CardTransferDto(sourceId, targetId, BigDecimal.valueOf(50));

        when(cardRepository.findAllById(Set.of(sourceId, targetId))).thenReturn(List.of(source, target));
        cardService.transfer(accountId, dto);

        assertEquals(BigDecimal.valueOf(50), source.getBalance());
        assertEquals(BigDecimal.valueOf(50), target.getBalance());
        verify(cardRepository).saveAll(List.of(source, target));
    }

    @Test
    void testTransfer_NotEnoughFunds() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        Card source = new Card();
        source.setId(sourceId);
        source.setStatus(CardStatus.ACTIVE);
        source.setBalance(BigDecimal.valueOf(10));
        Account acc = new Account(); acc.setId(accountId);
        source.setAccount(acc);

        Card target = new Card();
        target.setId(targetId);
        target.setStatus(CardStatus.ACTIVE);
        target.setBalance(BigDecimal.ZERO);
        target.setAccount(acc);

        CardTransferDto dto = new CardTransferDto(sourceId, targetId, BigDecimal.valueOf(50));

        when(cardRepository.findAllById(Set.of(sourceId, targetId))).thenReturn(List.of(source, target));
        assertThrows(ElementNotFoundException.class, () -> cardService.transfer(accountId, dto));
    }

    @Test
    void testChangeCardStatus_Success() {
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        cardService.changeCardStatus(cardId, CardStatus.BLOCKED);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void testSetBlockRequest_Success() {
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);
        card.setAccount(new Account());
        card.getAccount().setId(accountId);

        CardBlockedRequestDto dto = new CardBlockedRequestDto(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        cardService.setBlockRequest(accountId, dto);

        assertNotNull(card.getBlockedRequestAt());
        verify(cardRepository).save(card);
    }

    @Test
    void testSetBlockRequest_AccessDenied() {
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);
        card.setAccount(new Account());
        card.getAccount().setId(UUID.randomUUID());

        CardBlockedRequestDto dto = new CardBlockedRequestDto(cardId);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        assertThrows(AccessException.class, () -> cardService.setBlockRequest(accountId, dto));
    }
}
