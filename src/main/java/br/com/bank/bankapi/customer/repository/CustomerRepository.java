package br.com.bank.bankapi.customer.repository;

import br.com.bank.bankapi.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByUserId(UUID uuid);

    boolean existsByDocument(String document);

    Optional<Customer> findByUserId(UUID userId);
}