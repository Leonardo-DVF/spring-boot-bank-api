package br.com.bank.bankapi.transaction.dto;

import br.com.bank.bankapi.transaction.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionDTO(
        @NotNull UUID accountId,

        @NotNull TransactionType type,

        @NotNull
        @DecimalMin(value = "0.01", inclusive = true)
        BigDecimal amount,

        UUID toAccountId,
        String description
) {}
