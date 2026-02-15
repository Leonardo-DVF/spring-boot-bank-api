package br.com.bank.bankapi.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerDTO(
        @NotBlank
        @Size(min = 3, max = 120)
        String fullName
) {}
