package br.com.bank.bankapi.mapper;

import br.com.bank.bankapi.dto.user.RegisterDTO;
import br.com.bank.bankapi.model.user.User;

public final class UserMapper {
    private UserMapper() {}

    public static User toEntity(RegisterDTO dto, String encryptedPassword) {
        return new User(dto.username(), dto.email(), encryptedPassword, dto.role());
    }
}
