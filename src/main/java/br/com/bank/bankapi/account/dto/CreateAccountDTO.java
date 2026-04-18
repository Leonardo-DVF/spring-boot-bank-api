package br.com.bank.bankapi.account.dto;

import br.com.bank.bankapi.account.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to create a new bank account.")
public record CreateAccountDTO(

        @Schema(description = "Account agency number.", example = "0001")
        @NotNull
        @Size(max = 10)
        String agency,

        @Schema(description = "Account number.", example = "123456-7")
        @NotNull
        @Size(max = 20)
        String number,

        @Schema(description = "Type of the account.", example = "CHECKING")
        @NotNull
        AccountType type
) {}