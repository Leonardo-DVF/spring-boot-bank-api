package br.com.bank.bankapi.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Amount data used for deposit or withdrawal operations.")
public record AccountAmountDTO(

        @Schema(description = "Monetary amount for the operation.", example = "150.00")
        @NotNull
        @DecimalMin(value = "0.01", inclusive = true)
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount
) {}