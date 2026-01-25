package br.com.bank.bankapi.auth.mapper;

import br.com.bank.bankapi.auth.dto.RegisterDTO;
import br.com.bank.bankapi.user.model.User;

public final class UserMapper {
    private UserMapper() {}

    public static User toEntity(RegisterDTO dto, String encryptedPassword) {
        return new User(dto.username(), dto.email(), encryptedPassword, dto.role());
    }
}
