package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Account;
import com.example.bankcards.enums.Role;
import com.example.bankcards.enums.AccountStatus;
import com.example.bankcards.exception.BlockedStatusException;
import com.example.bankcards.mappers.AccountMapper;
import com.example.bankcards.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    private final UUID accountId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveAccount() {
        AccountCreateDto dto = new AccountCreateDto("test", "pass", "pass", "test@Test.tu", "dddd","dddd");
        Account account = new Account();
        when(accountMapper.dtoToEntity(dto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        String result = accountService.create(dto, Account::getUsername);
        assertNull(result); // getUsername returns null (not set in mock)
        verify(accountRepository).save(account);
    }

    @Test
    void update_ShouldUpdateExistingAccount() {
        AccountUpdateDto dto = new AccountUpdateDto(accountId, "lastName","newName","test@test.ru");
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        accountService.update(dto, Function.identity());

        verify(accountMapper).updateEntity(dto, account);
        verify(accountRepository).save(account);
    }

    @Test
    void update_ShouldThrow_WhenAccountNotFound() {
        AccountUpdateDto dto = new AccountUpdateDto(accountId, "newName", "dd", "test@test.ru");
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.update(dto, Function.identity()));
    }

    @Test
    void findById_ShouldReturnMappedAccount() {
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        String result = accountService.findById(accountId, acc -> "OK");
        assertEquals("OK", result);
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> accountService.findById(accountId, Function.identity()));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        Account account = new Account();
        when(accountRepository.findByUsername("test")).thenReturn(Optional.of(account));

        assertEquals(account, accountService.loadUserByUsername("test"));
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenNotFound() {
        when(accountRepository.findByUsername("test")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername("test"));
    }

    @Test
    void getAccountBy_ShouldThrow_WhenAccountBlocked() {
        AuthorizationData data = new AuthorizationData("test", "pass");
        Account account = new Account();
        account.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findByUsername("test")).thenReturn(Optional.of(account));

        assertThrows(BlockedStatusException.class, () -> accountService.getAccountBy(data));
    }

    @Test
    void getAccountBy_ShouldReturnAccountDto_WhenCredentialsValid() {
        AuthorizationData data = new AuthorizationData("test", "pass");
        Account account = new Account();
        account.setUsername("test");
        account.setStatus(AccountStatus.ACTIVE);
        account.setPassword("encoded");
        AccountDto dto = new AccountDto();

        when(accountRepository.findByUsername("test")).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches("passtest", "encoded")).thenReturn(true);
        when(accountMapper.entityToDto(account)).thenReturn(dto);

        AccountDto result = accountService.getAccountBy(data);
        assertEquals(dto, result);
    }

    @Test
    void getAccountBy_ShouldThrow_WhenPasswordInvalid() {
        AuthorizationData data = new AuthorizationData("test", "wrong");
        Account account = new Account();
        account.setStatus(AccountStatus.ACTIVE);
        account.setUsername("test");
        account.setPassword("encoded");

        when(accountRepository.findByUsername("test")).thenReturn(Optional.of(account));
        when(bCryptPasswordEncoder.matches("wrongtest", "encoded")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> accountService.getAccountBy(data));
    }

    @Test
    void updateRole_ShouldChangeUserRole() {
        Account account = new Account();
        RoleUpdateDto dto = new RoleUpdateDto(accountId, Role.ADMIN);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.updateRole(dto);

        assertEquals(Role.ADMIN, account.getRole());
        verify(accountRepository).save(account);
    }

    @Test
    void deleteById_ShouldDeleteAccount() {
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteById(accountId);

        verify(accountRepository).delete(account);
    }

    @Test
    void deleteById_ShouldThrow_WhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> accountService.deleteById(accountId));
    }

    @Test
    void getPages_ShouldReturnPage() {
        Specification<Account> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Account account = new Account();
        Page<Account> page = new PageImpl<>(List.of(account));

        when(accountRepository.findAll(spec, pageable)).thenReturn(page);

        Page<String> result = accountService.getPages(spec, pageable, acc -> "ok");
        assertEquals(1, result.getTotalElements());
    }
}
