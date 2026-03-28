package br.com.bank.bankapi.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositDTO(
        @NotNull @DecimalMin(value = "0.01")BigDecimal amount
) {}