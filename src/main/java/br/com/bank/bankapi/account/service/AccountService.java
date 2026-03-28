package br.com.bank.bankapi.account.service;

import br.com.bank.bankapi.account.dto.AmountDTO;
import br.com.bank.bankapi.account.dto.CreateAccountDTO;
import br.com.bank.bankapi.account.dto.AccountResponseDTO;
import br.com.bank.bankapi.account.dto.UpdateAccountStatusDTO;
import br.com.bank.bankapi.account.enums.Status;
import br.com.bank.bankapi.account.mapper.AccountMapper;
import br.com.bank.bankapi.account.model.Account;
import br.com.bank.bankapi.account.repository.AccountRepository;
import br.com.bank.bankapi.customer.model.Customer;
import br.com.bank.bankapi.customer.repository.CustomerRepository;
import br.com.bank.bankapi.exception.core.ConflictException;
import br.com.bank.bankapi.exception.core.ForbiddenOperationException;
import br.com.bank.bankapi.exception.core.ResourceNotFoundException;
import br.com.bank.bankapi.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountResponseDTO create(CreateAccountDTO dto, User user) {
        Customer customer = getCustomerByUser(user);

        Account account = AccountMapper.toEntity(dto, customer.getId());
        Account saved = accountRepository.save(account);

        return AccountMapper.toResponse(saved);
    }

    public AccountResponseDTO getById(UUID accountId, User user) {
        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);

        return AccountMapper.toResponse(account);
    }

    public List<AccountResponseDTO> listMyAccounts(User user) {
        Customer customer = getCustomerByUser(user);

        return accountRepository.findAllByCustomerId(customer.getId())
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @Transactional
    public AccountResponseDTO updateStatus(UUID accountId, UpdateAccountStatusDTO dto, User user) {
        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);

        account.setStatus(dto.status());
        Account saved = accountRepository.save(account);

        return AccountMapper.toResponse(saved);
    }

    @Transactional
    public AccountResponseDTO deposit(UUID accountId, AmountDTO dto, User user) {
        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);
        validateActiveAccount(account);

        BigDecimal newBalance = account.getBalance().add(dto.amount());
        account.setBalance(newBalance);

        Account saved = accountRepository.save(account);
        return AccountMapper.toResponse(saved);
    }

    @Transactional
    public AccountResponseDTO withdraw(UUID accountId, AmountDTO dto, User user) {
        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);
        validateActiveAccount(account);

        if (account.getBalance().compareTo(dto.amount()) < 0) {
            throw new ConflictException("Insufficient funds");
        }

        BigDecimal newBalance = account.getBalance().subtract(dto.amount());
        account.setBalance(newBalance);

        Account saved = accountRepository.save(account);
        return AccountMapper.toResponse(saved);
    }

    private Customer getCustomerByUser(User user) {
        return customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private void validateAccountOwnership(Account account, Customer customer) {
        if (!account.getCustomerId().equals(customer.getId())) {
            throw new ForbiddenOperationException("You do not have permission to access this account");
        }
    }

    private void validateActiveAccount(Account account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new ConflictException("Account is not active");
        }
    }
}