package br.com.bank.bankapi.account.mapper;

import br.com.bank.bankapi.account.dto.AccountResponseDTO;
import br.com.bank.bankapi.account.dto.CreateAccountDTO;
import br.com.bank.bankapi.account.enums.AccountStatus;
import br.com.bank.bankapi.account.model.Account;

import java.util.UUID;

public final class AccountMapper {

    private AccountMapper() {}

    public static Account toEntity(CreateAccountDTO dto, UUID customerId) {
        return new Account(
                customerId,
                dto.agency(),
                dto.number(),
                dto.type(),
                AccountStatus.ACTIVE
        );
    }

    public static AccountResponseDTO toResponse(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getCustomerId(),
                account.getAgency(),
                account.getNumber(),
                account.getType(),
                account.getStatus(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
