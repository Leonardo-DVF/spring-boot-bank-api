package br.com.bank.bankapi.transaction.dto;

import br.com.bank.bankapi.transaction.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Transaction response data.")
public record TransactionResponseDTO(

        @Schema(description = "Transaction identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Source account identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID accountId,

        @Schema(description = "Transaction type", example = "TRANSFER")
        TransactionType type,

        @Schema(description = "Transaction amount", example = "250.00")
        BigDecimal amount,

        @Schema(description = "Account balance before transaction", example = "1000.00")
        BigDecimal balanceBefore,

        @Schema(description = "Account balance after transaction", example = "750.00")
        BigDecimal balanceAfter,

        @Schema(description = "Destination account identifier, when applicable", example = "987e6543-e21b-12d3-a456-426614174999", nullable = true)
        UUID toAccountId,

        @Schema(description = "Transaction description", example = "Transfer to another account")
        String description,

        @Schema(description = "Transaction creation timestamp", example = "2026-04-09T18:30:00Z")
        Instant createdAt
) {}