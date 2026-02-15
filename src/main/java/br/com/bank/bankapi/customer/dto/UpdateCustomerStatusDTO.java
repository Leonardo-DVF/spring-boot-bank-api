package br.com.bank.bankapi.customer.dto;

import br.com.bank.bankapi.customer.enums.CustomerStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerStatusDTO(
        @NotNull CustomerStatus status
) {}
