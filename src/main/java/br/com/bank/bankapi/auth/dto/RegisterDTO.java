package br.com.bank.bankapi.auth.dto;

import br.com.bank.bankapi.user.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to register a new user.")
public record RegisterDTO(

        @Schema(description = "Unique username of the user.", example = "leo123")
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @Schema(description = "User email address.", example = "leo@email.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "User password.", example = "Strongpassword123")
        @NotBlank
        @Size(min = 6, max = 72)
        String password,

        @Schema(description = "Role assigned to the user.", example = "ROLE_CLIENT")
        @NotNull
        UserRole role) {
}