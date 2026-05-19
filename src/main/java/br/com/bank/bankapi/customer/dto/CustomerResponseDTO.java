package br.com.bank.bankapi.customer.dto;

import br.com.bank.bankapi.customer.enums.CustomerStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response returned with customer details.")
public record CustomerResponseDTO(

        @Schema(description = "Unique identifier of the customer.", example = "4be6cbe7-1b86-471e-8382-9e0b70f7dc47")
        UUID id,

        @Schema(description = "Full name of the customer.", example = "Leonardo Ferreira")
        String fullName,

        @Schema(description = "Customer CPF document number.", example = "11861069057")
        String document,

        @Schema(description = "Current status of the customer.", example = "ACTIVE")
        CustomerStatus status,

        @Schema(description = "Identifier of the user associated with the customer.", example = "a2806e11-ce82-4254-96af-0eabf34f6b50")
        UUID userId,

        @Schema(description = "Date and time when the customer was created.", example = "2026-04-07 09:03:46.461 -0300")
        Instant createdAt,

        @Schema(description = "Date and time of the last customer update.", example = "2026-04-07 09:03:46.461 -0300")
        Instant updatedAt

) {}
