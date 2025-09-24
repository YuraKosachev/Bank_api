package com.example.bankcards.util;

import com.example.bankcards.entity.Account;
import com.example.bankcards.enums.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.UUID;

public final class PermissionUtils {
    public static boolean inAdminRoleOrOwner(UsernamePasswordAuthenticationToken token, UUID accountId){
        if(inRole(token, Role.ADMIN))
        {
            return true;
        }
        var account = (Account)token.getPrincipal();
        return accountId.equals(account.getId());
    }

    public static boolean inRole(UsernamePasswordAuthenticationToken token, Role role){
        return token.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.getRoleWithPrefix()));
    }

    public static UUID getAccountId(UsernamePasswordAuthenticationToken token){
        return ((Account)token.getPrincipal()).getId();
    }
}
