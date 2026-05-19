package br.com.bank.bankapi.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data used to update the authenticated customer's information.")
public record UpdateCustomerDTO(

        @Schema(description = "Updated full name of the customer.", example = "Leonardo Ferreira")
        @NotBlank
        @Size(min = 3, max = 120)
        String fullName
) {}
