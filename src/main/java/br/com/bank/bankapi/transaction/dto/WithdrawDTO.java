package br.com.bank.bankapi.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Data required to perform a withdrawal.")
public record WithdrawDTO(

        @Schema(
                description = "Identifier of the source account.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "Account id is required")
        UUID accountId,

        @Schema(
                description = "Withdrawal amount.",
                example = "100.00"
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        @Digits(integer = 17, fraction = 2, message = "Amount must have up to 17 integer digits and 2 decimal places")
        BigDecimal amount,

        @Schema(
                description = "Optional withdrawal description.",
                example = "ATM withdrawal"
        )
        @Size(max = 255, message = "Description must have at most 255 characters")
        String description
) {}