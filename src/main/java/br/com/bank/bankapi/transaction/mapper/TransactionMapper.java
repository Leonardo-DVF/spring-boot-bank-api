package br.com.bank.bankapi.transaction.mapper;

import br.com.bank.bankapi.transaction.dto.CreateTransactionDTO;
import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.enums.TransactionType;
import br.com.bank.bankapi.transaction.model.Transaction;

import java.time.Instant;

public class TransactionMapper {

    private TransactionMapper() {}

    public static Transaction toEntity(CreateTransactionDTO dto) {
        var toAccountId = (dto.type() == TransactionType.TRANSFER)
                ? dto.toAccountId()
                : null;

        return new Transaction(
                dto.accountId(),
                dto.type(),
                dto.amount(),
                null,
                null,
                dto.description(),
                toAccountId
        );
    }


    public static TransactionResponseDTO toResponse(Transaction tx) {
        return new TransactionResponseDTO(
                tx.getId(),
                tx.getAccountId(),
                tx.getType(),
                tx.getAmount(),
                tx.getBalanceBefore(),
                tx.getBalanceAfter(),
                tx.getToAccountId(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}
