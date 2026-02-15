package br.com.bank.bankapi.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CreateCustomerDTO(
        @NotBlank
        @Size(min = 3, max = 120)
        String fullName,

        @NotBlank
        @CPF
        String document
) {}
