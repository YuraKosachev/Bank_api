package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Account;
import com.example.bankcards.enums.AccountStatus;
import com.example.bankcards.exception.BlockedStatusException;
import com.example.bankcards.interfaces.services.AccountService;
import com.example.bankcards.mappers.AccountMapper;
import com.example.bankcards.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl
        implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public <T> T create(AccountCreateDto account, Function<Account, T> mapper) {
        var entity = accountRepository.save(accountMapper.dtoToEntity(account));
        return mapper.apply(entity);
    }

    @Override
    public <T> T update(AccountUpdateDto account, Function<Account, T> mapper) {
        var entity = accountRepository.findById(account.id())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        accountMapper.updateEntity(account, entity);
        return mapper.apply(accountRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T findById(UUID id, Function<Account, T> mapper) {
        return accountRepository.findById(id)
                .map(mapper)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountBy(AuthorizationData data) {
        var account = (Account) loadUserByUsername(data.login());
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new BlockedStatusException("Your account is blocked");
        }
        if (!bCryptPasswordEncoder.matches(data.password().concat(data.login()), account.getPassword())) {
            throw new UsernameNotFoundException("Username or password is incorrect");
        }
        return accountMapper.entityToDto(account);
    }

    @Override
    public void updateRole(RoleUpdateDto roleUpdateDto) {
        var entity = accountRepository.findById(roleUpdateDto.accountId())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        entity.setRole(roleUpdateDto.role());
        accountRepository.save(entity);
    }

    @Override
    public void deleteById(UUID id) {
        var entity = accountRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        accountRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Page<T> getPages(Specification<Account> specification, Pageable pageable, Function<Account, T> mapper) {
        return accountRepository.findAll(specification, pageable).map(mapper);
    }
}