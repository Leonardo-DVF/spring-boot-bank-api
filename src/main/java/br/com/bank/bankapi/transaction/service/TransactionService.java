package br.com.bank.bankapi.transaction.service;

import br.com.bank.bankapi.account.enums.AccountStatus;
import br.com.bank.bankapi.account.model.Account;
import br.com.bank.bankapi.account.repository.AccountRepository;
import br.com.bank.bankapi.customer.model.Customer;
import br.com.bank.bankapi.customer.repository.CustomerRepository;
import br.com.bank.bankapi.exception.core.ConflictException;
import br.com.bank.bankapi.exception.core.ForbiddenOperationException;
import br.com.bank.bankapi.exception.core.ResourceNotFoundException;
import br.com.bank.bankapi.transaction.dto.DepositDTO;
import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.dto.TransferDTO;
import br.com.bank.bankapi.transaction.dto.WithdrawDTO;
import br.com.bank.bankapi.transaction.enums.TransactionType;
import br.com.bank.bankapi.transaction.mapper.TransactionMapper;
import br.com.bank.bankapi.transaction.model.Transaction;
import br.com.bank.bankapi.transaction.repository.TransactionRepository;
import br.com.bank.bankapi.user.model.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            CustomerRepository customerRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public TransactionResponseDTO deposit(DepositDTO dto, User user) {
        UUID userId = user.getId();

        validateAmount(dto.amount());

        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(dto.accountId());

        validateAccountOwnership(account, customer);
        validateActiveAccount(account);

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(dto.amount());

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                account.getId(),
                TransactionType.DEPOSIT,
                dto.amount(),
                balanceBefore,
                balanceAfter,
                null,
                normalizeDescription(dto.description())
        );

        Transaction saved = transactionRepository.save(transaction);

        log.info("Deposit completed successfully. userId={} accountId={} amount={} balanceBefore={} balanceAfter={}",
                userId, account.getId(), dto.amount(), balanceBefore, balanceAfter);

        return TransactionMapper.toResponse(saved);
    }

    @Transactional
    public TransactionResponseDTO withdraw(WithdrawDTO dto, User user) {
        UUID userId = user.getId();

        validateAmount(dto.amount());

        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(dto.accountId());

        validateAccountOwnership(account, customer);
        validateActiveAccount(account);

        BigDecimal balanceBefore = account.getBalance();

        if (balanceBefore.compareTo(dto.amount()) < 0) {
            log.warn("Withdrawal denied due to insufficient funds. userId={} accountId={} amount={} balance={}",
                    userId, account.getId(), dto.amount(), balanceBefore);
            throw new ConflictException("Insufficient funds");
        }

        BigDecimal balanceAfter = balanceBefore.subtract(dto.amount());

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                account.getId(),
                TransactionType.WITHDRAW,
                dto.amount(),
                balanceBefore,
                balanceAfter,
                null,
                normalizeDescription(dto.description())
        );

        Transaction saved = transactionRepository.save(transaction);

        log.info("Withdrawal completed successfully. userId={} accountId={} amount={} balanceBefore={} balanceAfter={}",
                userId, account.getId(), dto.amount(), balanceBefore, balanceAfter);

        return TransactionMapper.toResponse(saved);
    }

    @Transactional
    public TransactionResponseDTO transfer(TransferDTO dto, User user) {
        UUID userId = user.getId();

        validateAmount(dto.amount());

        Customer customer = getCustomerByUser(user);
        Account sourceAccount = getAccountById(dto.sourceAccountId());

        validateAccountOwnership(sourceAccount, customer);
        validateActiveAccount(sourceAccount);

        Account destinationAccount = accountRepository
                .findByAgencyAndNumber(dto.destinationAgency(), dto.destinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account", "agency/number", dto.destinationAgency() + "/" + dto.destinationAccountNumber()
                ));

        validateActiveAccount(destinationAccount);

        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            log.warn("Transfer denied because source and destination accounts are the same. userId={} accountId={}",
                    userId, sourceAccount.getId());
            throw new ConflictException("Cannot transfer to the same account");
        }

        BigDecimal sourceBalanceBefore = sourceAccount.getBalance();

        if (sourceBalanceBefore.compareTo(dto.amount()) < 0) {
            log.warn("Transfer denied due to insufficient funds. userId={} sourceAccountId={} amount={} balance={}",
                    userId, sourceAccount.getId(), dto.amount(), sourceBalanceBefore);
            throw new ConflictException("Insufficient funds");
        }

        BigDecimal sourceBalanceAfter = sourceBalanceBefore.subtract(dto.amount());
        BigDecimal destinationBalanceBefore = destinationAccount.getBalance();
        BigDecimal destinationBalanceAfter = destinationBalanceBefore.add(dto.amount());

        sourceAccount.setBalance(sourceBalanceAfter);
        destinationAccount.setBalance(destinationBalanceAfter);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        String normalizedDescription = normalizeDescription(dto.description());

        Transaction sourceTransaction = new Transaction(
                sourceAccount.getId(),
                TransactionType.TRANSFER,
                dto.amount(),
                sourceBalanceBefore,
                sourceBalanceAfter,
                destinationAccount.getId(),
                normalizedDescription
        );

        Transaction destinationTransaction = new Transaction(
                destinationAccount.getId(),
                TransactionType.TRANSFER,
                dto.amount(),
                destinationBalanceBefore,
                destinationBalanceAfter,
                sourceAccount.getId(),
                normalizedDescription
        );

        Transaction savedSourceTransaction = transactionRepository.save(sourceTransaction);
        transactionRepository.save(destinationTransaction);

        log.info("Transfer completed successfully. userId={} sourceAccountId={} destinationAccountId={} amount={} sourceBalanceBefore={} sourceBalanceAfter={} destinationBalanceBefore={} destinationBalanceAfter={}",
                userId,
                sourceAccount.getId(),
                destinationAccount.getId(),
                dto.amount(),
                sourceBalanceBefore,
                sourceBalanceAfter,
                destinationBalanceBefore,
                destinationBalanceAfter);

        return TransactionMapper.toResponse(savedSourceTransaction);
    }

    public TransactionResponseDTO getById(UUID transactionId, User user) {
        Customer customer = getCustomerByUser(user);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        Account account = getAccountById(transaction.getAccountId());
        validateAccountOwnership(account, customer);

        return TransactionMapper.toResponse(transaction);
    }

    public Page<TransactionResponseDTO> listByAccount(UUID accountId, Pageable pageable, User user) {
        Customer customer = getCustomerByUser(user);
        Account account = getAccountById(accountId);

        validateAccountOwnership(account, customer);

        log.info("Transaction history requested. userId={} accountId={} page={} size={}",
                user.getId(), accountId, pageable.getPageNumber(), pageable.getPageSize());

        return transactionRepository.findAllByAccountId(accountId, pageable)
                .map(TransactionMapper::toResponse);
    }

    private Customer getCustomerByUser(User user) {
        UUID userId = user.getId();

        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", userId.toString()));
    }

    private Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    private void validateAccountOwnership(Account account, Customer customer) {
        if (!account.getCustomerId().equals(customer.getId())) {
            log.warn("Operation denied because account does not belong to authenticated user. accountId={} customerId={} accountCustomerId={}",
                    account.getId(), customer.getId(), account.getCustomerId());
            throw new ForbiddenOperationException("You can't operate on this account");
        }
    }

    private void validateActiveAccount(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.warn("Operation denied because account is not active. accountId={} status={}",
                    account.getId(), account.getStatus());
            throw new ConflictException("Account is not active");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Operation denied because amount is invalid. amount={}", amount);
            throw new ConflictException("Amount must be greater than zero");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}