package com.example.bankcards.interfaces.services;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Account;
import com.example.bankcards.interfaces.services.base.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService,
        Creatable<AccountCreateDto, Account>,
        Updateable<AccountUpdateDto, Account>,
        Pagetable<Account>,
        Deletable,
        Searchable<Account>
{
    AccountDto getAccountBy(AuthorizationData data);
    void updateRole(RoleUpdateDto roleUpdateDto);
}
