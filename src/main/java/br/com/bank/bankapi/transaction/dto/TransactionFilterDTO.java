package br.com.bank.bankapi.transaction.dto;

import java.time.Instant;

public record TransactionFilterDTO(
        Instant from,
        Instant to
) {}
