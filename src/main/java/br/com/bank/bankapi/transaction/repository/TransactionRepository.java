package br.com.bank.bankapi.transaction.repository;

import br.com.bank.bankapi.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAllByAccountId(UUID accountId, Pageable pageable);

}
