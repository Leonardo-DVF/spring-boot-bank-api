package br.com.bank.bankapi.account.dto;

import br.com.bank.bankapi.account.enums.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountStatusDTO(
        @NotNull Status status
) {}
