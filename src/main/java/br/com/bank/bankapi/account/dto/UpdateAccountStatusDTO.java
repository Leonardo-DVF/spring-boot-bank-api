package br.com.bank.bankapi.account.dto;

import br.com.bank.bankapi.account.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data used to update the account status.")
public record UpdateAccountStatusDTO(

        @Schema(description = "New status of the account.", example = "ACTIVE")
        @NotNull
        AccountStatus status
) {}