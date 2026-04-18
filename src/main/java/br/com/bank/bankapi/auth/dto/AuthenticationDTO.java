package br.com.bank.bankapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials used to authenticate a user.")
public record AuthenticationDTO(

        @Schema(description = "Username used for login", example = "leo123")
        @NotBlank
        String username,

        @Schema(description = "Password used for login.", example = "Strongpassword123")
        @NotBlank
        String password
) {}