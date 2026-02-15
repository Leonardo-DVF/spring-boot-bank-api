package br.com.bank.bankapi.customer.dto;

import br.com.bank.bankapi.customer.enums.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String fullname,
        String document,
        CustomerStatus status,
        UUID userId,
        Instant createdAt,
        Instant updatedAt
) {}
