package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    @Query("select c from Card c where c.id = :cardId and c.account.id = :accountId")
    Optional<Card> findByAccountId(UUID accountId, UUID cardId);

    @Query("select c from Card c where c.id = :cardId and c.status = com.example.bankcards.enums.CardStatus.ACTIVE")
    Optional<Card> findActiveById(UUID cardId);

    @Query("select c from Card c where c.expiredIn <= :date AND c.status <> com.example.bankcards.enums.CardStatus.EXPIRED")
    List<Card> findByExpiredIn(LocalDate date);

    @Query("""
            select c from Card c 
              where c.blockedRequestAt is not null 
                          AND c.status NOT IN :statuses
                                      """)
    List<Card> findByRequestNotIn(@Param("statuses") Set<CardStatus> statuses);
}
