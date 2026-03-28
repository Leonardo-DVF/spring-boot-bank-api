package br.com.bank.bankapi.account.controller;

import br.com.bank.bankapi.account.dto.AccountResponseDTO;
import br.com.bank.bankapi.account.dto.AmountDTO;
import br.com.bank.bankapi.account.dto.CreateAccountDTO;
import br.com.bank.bankapi.account.dto.UpdateAccountStatusDTO;
import br.com.bank.bankapi.account.service.AccountService;
import br.com.bank.bankapi.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(
            @Valid @RequestBody CreateAccountDTO dto,
            @AuthenticationPrincipal User user
    ) {
        AccountResponseDTO created = accountService.create(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        AccountResponseDTO response = accountService.getById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(
            @AuthenticationPrincipal User user
    ) {
        List<AccountResponseDTO> accounts = accountService.listMyAccounts(user);
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountStatusDTO dto,
            @AuthenticationPrincipal User user
    ) {
        AccountResponseDTO updated = accountService.updateStatus(id, dto, user);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(
            @PathVariable UUID id,
            @Valid @RequestBody AmountDTO dto,
            @AuthenticationPrincipal User user
    ) {
        AccountResponseDTO updated = accountService.deposit(id, dto, user);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(
            @PathVariable UUID id,
            @Valid @RequestBody AmountDTO dto,
            @AuthenticationPrincipal User user
    ) {
        AccountResponseDTO updated = accountService.withdraw(id, dto, user);
        return ResponseEntity.ok(updated);
    }
}
