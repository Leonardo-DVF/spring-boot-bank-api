package br.com.bank.bankapi.transaction.service;

import br.com.bank.bankapi.account.enums.Status;
import br.com.bank.bankapi.account.model.Account;
import br.com.bank.bankapi.account.repository.AccountRepository;
import br.com.bank.bankapi.customer.model.Customer;
import br.com.bank.bankapi.customer.repository.CustomerRepository;
import br.com.bank.bankapi.exception.core.ForbiddenOperationException;
import br.com.bank.bankapi.exception.core.ResourceNotFoundException;
import br.com.bank.bankapi.exception.core.ConflictException;
import br.com.bank.bankapi.transaction.dto.CreateTransactionDTO;
import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.enums.TransactionType;
import br.com.bank.bankapi.transaction.mapper.TransactionMapper;
import br.com.bank.bankapi.transaction.model.Transaction;
import br.com.bank.bankapi.transaction.repository.TransactionRepository;
import br.com.bank.bankapi.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

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
    public TransactionResponseDTO create(CreateTransactionDTO dto, User user) {
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", user.getId().toString()));

        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", dto.accountId()));

        if (!account.getCustomerId().equals(customer.getId())) {
            throw new ForbiddenOperationException("You can't operate on this account");
        }

        if (account.getStatus() != Status.ACTIVE) {
            throw new ConflictException("Account is not active");
        }

        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictException("Amount must be greater than zero");
        }

        if (dto.type() == null) {
            throw new ConflictException("Transaction type is required");
        }

        return switch (dto.type()) {
            case DEPOSIT -> doDeposit(account, dto);
            case WITHDRAW -> doWithdraw(account, dto);
            case TRANSFER -> doTransfer(customer, account, dto);
        };
    }

    public TransactionResponseDTO getById(UUID transactionId, User user) {
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", user.getId().toString()));

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        Account account = accountRepository.findById(tx.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", tx.getAccountId()));

        if (!account.getCustomerId().equals(customer.getId())) {
            throw new ForbiddenOperationException("You can't access this transaction");
        }

        return TransactionMapper.toResponse(tx);
    }

    public List<TransactionResponseDTO> listByAccount(UUID accountId, User user) {
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", user.getId().toString()));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!account.getCustomerId().equals(customer.getId())) {
            throw new ForbiddenOperationException("You can't access transactions from this account");
        }

        return transactionRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    private TransactionResponseDTO doDeposit(Account account, CreateTransactionDTO dto) {
        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(dto.amount());

        account.setBalance(after);
        accountRepository.save(account);

        Transaction tx = TransactionMapper.toEntity(dto);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);

        Transaction saved = transactionRepository.save(tx);
        return TransactionMapper.toResponse(saved);
    }

    private TransactionResponseDTO doWithdraw(Account account, CreateTransactionDTO dto) {
        BigDecimal before = account.getBalance();

        if (before.compareTo(dto.amount()) < 0) {
            throw new ConflictException("Insufficient funds");
        }

        BigDecimal after = before.subtract(dto.amount());

        account.setBalance(after);
        accountRepository.save(account);

        Transaction tx = TransactionMapper.toEntity(dto);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);

        Transaction saved = transactionRepository.save(tx);
        return TransactionMapper.toResponse(saved);
    }

    private TransactionResponseDTO doTransfer(Customer customer, Account from, CreateTransactionDTO dto) {
        UUID toAccountId = dto.toAccountId();
        if (toAccountId == null) {
            throw new ConflictException("toAccountId is required for TRANSFER");
        }
        if (toAccountId.equals(from.getId())) {
            throw new ConflictException("Cannot transfer to the same account");
        }

        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", toAccountId));

        if (to.getStatus() != Status.ACTIVE) {
            throw new ConflictException("Destination account is not active");
        }

        BigDecimal fromBefore = from.getBalance();
        if (fromBefore.compareTo(dto.amount()) < 0) {
            throw new ConflictException("Insufficient funds");
        }
        BigDecimal fromAfter = fromBefore.subtract(dto.amount());

        BigDecimal toBefore = to.getBalance();
        BigDecimal toAfter = toBefore.add(dto.amount());

        from.setBalance(fromAfter);
        to.setBalance(toAfter);
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = TransactionMapper.toEntity(dto);
        tx.setBalanceBefore(fromBefore);
        tx.setBalanceAfter(fromAfter);

        Transaction saved = transactionRepository.save(tx);
        return TransactionMapper.toResponse(saved);
    }
}
