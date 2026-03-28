package br.com.bank.bankapi.account.dto;

import br.com.bank.bankapi.account.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAccountDTO(
        @NotNull @Size(max = 10) String agency,
        @NotNull @Size(max = 20) String number,
        @NotNull AccountType type
        ) {}
