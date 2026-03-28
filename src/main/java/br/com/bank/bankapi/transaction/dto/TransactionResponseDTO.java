package br.com.bank.bankapi.transaction.dto;

import br.com.bank.bankapi.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        UUID accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        UUID toAccountId,
        String description,
        Instant createdAt
) {}