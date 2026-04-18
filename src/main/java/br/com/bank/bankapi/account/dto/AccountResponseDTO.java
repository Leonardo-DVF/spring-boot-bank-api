package br.com.bank.bankapi.account.dto;

import br.com.bank.bankapi.account.enums.AccountType;
import br.com.bank.bankapi.account.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response returned with account details.")
public record AccountResponseDTO(

        @Schema(description = "Unique identifier of the account.", example = "5a2c93d1-5d8b-48a7-936f-d88699ab4577")
        UUID id,

        @Schema(description = "Identifier of the customer associated with the account.", example = "4be6cbe7-1b86-471e-8382-9e0b70f7dc47")
        UUID customerId,

        @Schema(description = "Account agency number.", example = "0003")
        String agency,

        @Schema(description = "Account number.", example = "123456-7")
        String number,

        @Schema(description = "Type of the account.", example = "CHECKING")
        AccountType type,

        @Schema(description = "Current status of the account.", example = "ACTIVE")
        AccountStatus status,

        @Schema(description = "Current account balance.", example = "1500.00")
        BigDecimal balance,

        @Schema(description = "Date and time when the account was created.", example = "2026-04-07 09:07:43.808 -0300")
        Instant createdAt,

        @Schema(description = "Date and time of the last account update.", example = "2026-04-07 09:07:43.808 -0300")
        Instant updatedAt
) {}