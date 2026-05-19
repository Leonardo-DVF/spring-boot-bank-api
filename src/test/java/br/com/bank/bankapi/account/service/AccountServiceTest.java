package br.com.bank.bankapi.account.service;

import br.com.bank.bankapi.account.dto.CreateAccountDTO;
import br.com.bank.bankapi.account.enums.AccountStatus;
import br.com.bank.bankapi.account.enums.AccountType;
import br.com.bank.bankapi.account.model.Account;
import br.com.bank.bankapi.account.repository.AccountRepository;
import br.com.bank.bankapi.customer.enums.CustomerStatus;
import br.com.bank.bankapi.customer.model.Customer;
import br.com.bank.bankapi.customer.repository.CustomerRepository;
import br.com.bank.bankapi.exception.core.ConflictException;
import br.com.bank.bankapi.exception.core.ForbiddenOperationException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, customerRepository);
    }

    @Test
    void createShouldRejectDuplicatedAgencyAndNumber() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        CreateAccountDTO dto = new CreateAccountDTO("0001", "123456-7", AccountType.CHECKING);

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.existsByAgencyAndNumber(dto.agency(), dto.number())).thenReturn(true);

        assertThrows(ConflictException.class, () -> accountService.create(dto, user));

        verify(accountRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getByIdShouldRejectAccountFromAnotherCustomer() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        Account account = account(UUID.randomUUID(), UUID.randomUUID(), AccountStatus.ACTIVE, "100.00");

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(ForbiddenOperationException.class, () -> accountService.getById(account.getId(), user));
    }

    @Test
    void updateStatusShouldUpdateAccountStatus() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), user.getId());
        Account account = account(UUID.randomUUID(), customer.getId(), AccountStatus.ACTIVE, "100.00");

        when(customerRepository.findByUserId(user.getId())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        accountService.updateStatus(account.getId(), new br.com.bank.bankapi.account.dto.UpdateAccountStatusDTO(AccountStatus.BLOCKED), user);

        assertEquals(AccountStatus.BLOCKED, account.getStatus());
        verify(accountRepository).save(account);
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
