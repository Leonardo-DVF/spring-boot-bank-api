package br.com.bank.bankapi.customer.dto;

import br.com.bank.bankapi.customer.enums.CustomerStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data used to update the customer status.")
public record UpdateCustomerStatusDTO(

        @Schema(description = "New status of the customer.", example = "ACTIVE")
        @NotNull
        CustomerStatus status
) {}