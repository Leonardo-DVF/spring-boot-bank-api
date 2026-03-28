package br.com.bank.bankapi.transaction.controller;

import br.com.bank.bankapi.transaction.dto.CreateTransactionDTO;
import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.service.TransactionService;
import br.com.bank.bankapi.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(
            @Valid @RequestBody CreateTransactionDTO dto,
            @AuthenticationPrincipal User user
    ) {
        TransactionResponseDTO created = transactionService.create(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(transactionService.getById(id, user));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> listByAccount(
            @PathVariable UUID accountId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(transactionService.listByAccount(accountId, user));
    }
}

