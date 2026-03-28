package br.com.bank.bankapi.account.dto;

import br.com.bank.bankapi.account.enums.AccountType;
import br.com.bank.bankapi.account.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponseDTO(
    UUID id,
    UUID customerId,
    String agency,
    String number,
    AccountType type,
    Status status,
    BigDecimal balance,
    Instant createdAt,
    Instant updatedAt
) {}