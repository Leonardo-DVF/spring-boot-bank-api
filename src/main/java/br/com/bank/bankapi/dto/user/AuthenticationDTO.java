package br.com.bank.bankapi.dto.user;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank String username,
        @NotBlank String password) {
}