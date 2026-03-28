package br.com.bank.bankapi.account.repository;

import br.com.bank.bankapi.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findAllByCustomerId(UUID customerId);

}