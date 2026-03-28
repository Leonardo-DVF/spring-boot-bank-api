package br.com.bank.bankapi.transaction.repository;

import br.com.bank.bankapi.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
