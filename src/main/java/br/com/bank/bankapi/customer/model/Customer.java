package br.com.bank.bankapi.customer.model;

import br.com.bank.bankapi.customer.enums.CustomerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 120)
    @Column(nullable = false, length = 120)
    private String fullName;

    @NotBlank
    @CPF
    @Column(nullable = false, unique = true, length = 11)
    private String document;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private CustomerStatus status;

    @NotNull
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Customer() {}

    public Customer(String fullName, String document, CustomerStatus status, UUID userId) {
        this.fullName = fullName;
        this.document = document;
        this.status = status;
        this.userId = userId;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
