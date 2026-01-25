package br.com.bank.bankapi.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank String username,
        @NotBlank String password) {
}