package com.example.bankcards.repository;

import com.example.bankcards.entity.Account;
import com.example.bankcards.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByUsername(String username);
    @Query("select a from Account a where a.id = :id and a.status = com.example.bankcards.enums.AccountStatus.ACTIVE")
    Optional<Account> getActiveAccountById(UUID id);

    @Query("select a from Account a where a.role = :role")
    List<Account> getAllByRole(Role role);
}
