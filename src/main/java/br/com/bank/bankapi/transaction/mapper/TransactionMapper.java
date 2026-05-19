package br.com.bank.bankapi.transaction.mapper;

import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.model.Transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getToAccountId(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}