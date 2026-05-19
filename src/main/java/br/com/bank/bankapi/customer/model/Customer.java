package br.com.bank.bankapi.customer.model;

import br.com.bank.bankapi.customer.enums.CustomerStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "document", nullable = false, unique = true, length = 11)
    private String document;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private CustomerStatus status;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
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

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDocument() {
        return document;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStatus(CustomerStatus status) { this.status = status;
    }
}