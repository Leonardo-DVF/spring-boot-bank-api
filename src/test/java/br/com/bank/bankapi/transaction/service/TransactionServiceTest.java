package br.com.bank.bankapi.transaction.service;

import br.com.bank.bankapi.account.enums.AccountStatus;
import br.com.bank.bankapi.account.enums.AccountType;
import br.com.bank.bankapi.account.model.Account;
import br.com.bank.bankapi.account.repository.AccountRepository;
import br.com.bank.bankapi.customer.enums.CustomerStatus;
import br.com.bank.bankapi.customer.model.Customer;
import br.com.bank.bankapi.customer.repository.CustomerRepository;
import br.com.bank.bankapi.exception.core.ConflictException;
import br.com.bank.bankapi.exception.core.ForbiddenOperationException;
import br.com.bank.bankapi.transaction.dto.DepositDTO;
import br.com.bank.bankapi.transaction.dto.TransferDTO;
import br.com.bank.bankapi.transaction.dto.WithdrawDTO;
import br.com.bank.bankapi.transaction.repository.TransactionRepository;
import br.com.bank.bankapi.user.enums.UserRole;
import br.com.bank.bankapi.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(
                transactionRepository,
                accountRepository,
                customerRepository
        );
    }

    @Test
    void depositShouldRejectInvalidAmount() {
        User user = user(UUID.randomUUID());
        DepositDTO dto = new DepositDTO(UUID.randomUUID(), BigDecimal.ZERO, "Invalid deposit");

        assertThrows(ConflictException.class, () -> transactionService.deposit(dto, user));

        verifyNoInteractions(customerRepository, accountRepository, transactionRepository);
    }

    @Test
    void withdrawShouldRejectInsufficientFunds() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        Account account = account(UUID.randomUUID(), customer.getId(), AccountStatus.ACTIVE, "50.00");
        WithdrawDTO dto = new WithdrawDTO(account.getId(), new BigDecimal("100.00"), "ATM withdrawal");

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(ConflictException.class, () -> transactionService.withdraw(dto, user));

        verify(accountRepository, never()).save(account);
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void transferShouldRejectSameSourceAndDestinationAccount() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        Account sourceAccount = account(UUID.randomUUID(), customer.getId(), AccountStatus.ACTIVE, "200.00");
        TransferDTO dto = new TransferDTO(
                sourceAccount.getId(),
                sourceAccount.getAgency(),
                sourceAccount.getNumber(),
                new BigDecimal("50.00"),
                "Same account transfer"
        );

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAgencyAndNumber(sourceAccount.getAgency(), sourceAccount.getNumber()))
                .thenReturn(Optional.of(sourceAccount));

        assertThrows(ConflictException.class, () -> transactionService.transfer(dto, user));

        verify(accountRepository, never()).save(sourceAccount);
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void withdrawShouldRejectInactiveAccount() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        Account account = account(UUID.randomUUID(), customer.getId(), AccountStatus.BLOCKED, "200.00");
        WithdrawDTO dto = new WithdrawDTO(account.getId(), new BigDecimal("50.00"), "Blocked account withdrawal");

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(ConflictException.class, () -> transactionService.withdraw(dto, user));

        verify(accountRepository, never()).save(account);
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void depositShouldRejectAccountFromAnotherCustomer() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        Account account = account(UUID.randomUUID(), UUID.randomUUID(), AccountStatus.ACTIVE, "200.00");
        DepositDTO dto = new DepositDTO(account.getId(), new BigDecimal("50.00"), "Unauthorized deposit");

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenOperationException.class, () -> transactionService.deposit(dto, user));

        verify(accountRepository, never()).save(account);
        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private User user(UUID id) {
        User user = new User("client", "client@email.com", "encoded-password", UserRole.ROLE_CLIENT);
        setField(user, "id", id);
        return user;
    }

    private Customer customer(UUID id, UUID userId) {
        Customer customer = new Customer("Client Name", "12345678901", CustomerStatus.ACTIVE, userId);
        setField(customer, "id", id);
        return customer;
    }

    private Account account(UUID id, UUID customerId, AccountStatus status, String balance) {
        Account account = new Account(customerId, "0001", "123456-7", AccountType.CHECKING, status);
        setField(account, "id", id);
        account.setBalance(new BigDecimal(balance));
        return account;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException("Could not set field " + fieldName, ex);
        }
    }
}
