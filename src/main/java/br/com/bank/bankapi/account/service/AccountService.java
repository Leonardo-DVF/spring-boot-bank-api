package br.com.bank.bankapi.account.service;

import br.com.bank.bankapi.account.dto.AccountAmountDTO;
import br.com.bank.bankapi.account.dto.CreateAccountDTO;
import br.com.bank.bankapi.account.dto.AccountResponseDTO;
import br.com.bank.bankapi.account.dto.UpdateAccountStatusDTO;
import br.com.bank.bankapi.account.enums.AccountStatus;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountResponseDTO create(CreateAccountDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Creating account requested by userId={} agency={} number={} type={}",
                userId, dto.agency(), dto.number(), dto.type());

        Customer customer = getCustomerByUser(user);

        if (accountRepository.existsByAgencyAndNumber(dto.agency(), dto.number())) {
            log.warn("Account creation blocked: agency and number already exist. agency={} number={} requestedByUserId={}",
                    dto.agency(), dto.number(), userId);
            throw new ConflictException("Account already exists for the provided agency and number");
        }

        Account account = AccountMapper.toEntity(dto, customer.getId());
        Account saved = accountRepository.save(account);

        log.info("Account created successfully: accountId={} customerId={} requestedByUserId={}",
                saved.getId(), customer.getId(), userId);

        return AccountMapper.toResponse(saved);
    }

    public AccountResponseDTO getById(UUID accountId, User user) {
        UUID userId = user.getId();

        log.info("Fetching account by id={} requestedByUserId={}", accountId, userId);

        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);

        log.info("Account fetched successfully: accountId={} requestedByUserId={}", accountId, userId);

        return AccountMapper.toResponse(account);
    }

    public List<AccountResponseDTO> listMyAccounts(User user) {
        UUID userId = user.getId();

        log.info("Listing accounts for userId={}", userId);

        Customer customer = getCustomerByUser(user);

        List<AccountResponseDTO> accounts = accountRepository.findAllByCustomerId(customer.getId())
                .stream()
                .map(AccountMapper::toResponse)
                .toList();

        log.info("Accounts listed successfully: userId={} totalAccounts={}", userId, accounts.size());

        return accounts;
    }

    @Transactional
    public AccountResponseDTO updateStatus(UUID accountId, UpdateAccountStatusDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Updating account status: accountId={} requestedByUserId={} newStatus={}",
                accountId, userId, dto.status());

        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);

        account.setStatus(dto.status());
        Account saved = accountRepository.save(account);

        log.info("Account status updated successfully: accountId={} requestedByUserId={} status={}",
                saved.getId(), userId, saved.getStatus());

        return AccountMapper.toResponse(saved);
    }

    @Transactional
    public AccountResponseDTO deposit(UUID accountId, AccountAmountDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Deposit requested: accountId={} requestedByUserId={} amount={}",
                accountId, userId, dto.amount());

        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);
        validateActiveAccount(account);

        BigDecimal newBalance = account.getBalance().add(dto.amount());
        account.setBalance(newBalance);

        Account saved = accountRepository.save(account);

        log.info("Deposit completed successfully: accountId={} requestedByUserId={} amount={} newBalance={}",
                saved.getId(), userId, dto.amount(), saved.getBalance());

        return AccountMapper.toResponse(saved);
    }

    @Transactional
    public AccountResponseDTO withdraw(UUID accountId, AccountAmountDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Withdrawal requested: accountId={} requestedByUserId={} amount={}",
                accountId, userId, dto.amount());

        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);
        validateActiveAccount(account);

        if (account.getBalance().compareTo(dto.amount()) < 0) {
            log.warn("Withdrawal blocked: insufficient funds. accountId={} requestedByUserId={} balance={} requestedAmount={}",
                    accountId, userId, account.getBalance(), dto.amount());
            throw new ConflictException("Insufficient funds");
        }

        BigDecimal newBalance = account.getBalance().subtract(dto.amount());
        account.setBalance(newBalance);

        Account saved = accountRepository.save(account);

        log.info("Withdrawal completed successfully: accountId={} requestedByUserId={} amount={} newBalance={}",
                saved.getId(), userId, dto.amount(), saved.getBalance());

        return AccountMapper.toResponse(saved);
    }

    private Customer getCustomerByUser(User user) {
        UUID userId = user.getId();

        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for userId={}", userId);
                    return new ResourceNotFoundException("Customer", "userId", userId.toString());
                });
    }

    private Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.warn("Account not found: accountId={}", accountId);
                    return new ResourceNotFoundException("Account", accountId);
                });
    }

    private void validateAccountOwnership(Account account, Customer customer) {
        if (!account.getCustomerId().equals(customer.getId())) {
            log.warn("Forbidden account access: accountId={} ownerCustomerId={} requestedCustomerId={}",
                    account.getId(), account.getCustomerId(), customer.getId());
            throw new ForbiddenOperationException("You do not have permission to access this account");
        }
    }

    private void validateActiveAccount(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.warn("Operation blocked: account is not active. accountId={} status={}",
                    account.getId(), account.getStatus());
            throw new ConflictException("Account is not active");
        }
    }
}