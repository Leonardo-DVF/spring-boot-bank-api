package br.com.bank.bankapi.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Data required to perform a transfer between accounts.")
public record TransferDTO(

        @Schema(
                description = "Identifier of the source account.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "Source account id is required")
        UUID sourceAccountId,

        @Schema(
                description = "Destination account agency.",
                example = "0001"
        )
        @NotBlank(message = "Destination agency is required")
        @Size(min = 1, max = 10, message = "Destination agency must have between 1 and 10 characters")
        String destinationAgency,

        @Schema(
                description = "Destination account number.",
                example = "123456-7"
        )
        @NotBlank(message = "Destination account number is required")
        @Size(min = 1, max = 20, message = "Destination account number must have between 1 and 20 characters")
        String destinationAccountNumber,

        @Schema(
                description = "Transfer amount.",
                example = "250.00"
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        @Digits(integer = 17, fraction = 2, message = "Amount must have up to 17 integer digits and 2 decimal places")
        BigDecimal amount,

        @Schema(
                description = "Optional transfer description.",
                example = "Transfer to savings account"
        )
        @Size(max = 255, message = "Description must have at most 255 characters")
        String description
) {}