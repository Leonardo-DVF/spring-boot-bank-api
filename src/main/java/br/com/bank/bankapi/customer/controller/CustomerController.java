package br.com.bank.bankapi.customer.controller;

import br.com.bank.bankapi.customer.dto.CreateCustomerDTO;
import br.com.bank.bankapi.customer.dto.CustomerResponseDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerStatusDTO;
import br.com.bank.bankapi.customer.service.CustomerService;
import br.com.bank.bankapi.user.model.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @Valid @RequestBody CreateCustomerDTO dto,
            @AuthenticationPrincipal User user
    ) {

        log.info("Customer creation requested by userId={}", user.getId());

        CustomerResponseDTO created = customerService.create(dto, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable UUID id, @AuthenticationPrincipal User user) {

        log.info("GET /customers/{} requested by userId={}", id, user.getId());

        CustomerResponseDTO response = customerService.getById(id, user);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<CustomerResponseDTO> me(
            @Valid @RequestBody UpdateCustomerDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("PATCH /customers/me requested by userId={} fields=[fullName:{}]",
                user.getId(),
                dto.fullName() != null && !dto.fullName().isBlank()
        );

        return ResponseEntity.ok(customerService.update(dto, user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerStatusDTO dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("PATCH /customers/{}/status requested by userId={} newStatus={}",
                id, user.getId(), dto.status());

        return ResponseEntity.ok(customerService.updateStatus(id, dto, user));
    }
}