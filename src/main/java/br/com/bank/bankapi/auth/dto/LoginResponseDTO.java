package br.com.bank.bankapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after successful user authentication.")
public record LoginResponseDTO(
        @Schema(
                description = "JWT token used to access protected endpoints.",
                example = "eyJhbGciOiJIUzI1NiJ9.example.token"
        )
        String token
) {}