package com.example.bankcards.entity;

import com.example.bankcards.constants.DbConstants;
import com.example.bankcards.entity.converters.ByteArrayConverter;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.util.HashUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = DbConstants.CARD_TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    String owner;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    CardStatus status;

    @Column(name = "card_number_encrypted", nullable = false, columnDefinition = "BYTEA")
    @Convert(converter = ByteArrayConverter.class)
    String cardNumber;

    @Column(name = "card_number_hash", nullable = false, length = 64)
    String cardNumberHash;

    @Temporal(TemporalType.DATE)
    @Column(name = "expired_in", nullable = false)
    LocalDate expiredIn;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "blocked_request_at")
    LocalDateTime blockedRequestAt;

    @Column(nullable = false)
    @PositiveOrZero(message = "Balance cannot be negative")
    BigDecimal balance;

    @PrePersist
    protected void onCreate() {
        if(status == null){
            status = CardStatus.ACTIVE;
        }
        if(balance == null){
            balance = BigDecimal.ZERO;
        }
        if(owner == null){
            owner = "%s %s".formatted(account.getLastName().toUpperCase(), account.getFirstName().toUpperCase());
        }
        if(cardNumberHash == null){
            cardNumberHash = HashUtils.sha256Hex(cardNumber);
        }
    }
}
