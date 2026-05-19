package br.com.bank.bankapi.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "Data required to create a new customer.")
public record CreateCustomerDTO(

        @Schema(description = "Full name of the customer.", example = "Leonardo Ferreira")
        @NotBlank
        @Size(min = 3, max = 120)
        String fullName,

        @Schema(description = "Customer CPF document number.", example = "21872010075")
        @NotBlank
        @CPF
        String document
) {}
