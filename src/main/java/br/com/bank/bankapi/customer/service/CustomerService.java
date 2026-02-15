package br.com.bank.bankapi.customer.service;

import br.com.bank.bankapi.customer.dto.CreateCustomerDTO;
import br.com.bank.bankapi.customer.dto.CustomerResponseDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerStatusDTO;
import br.com.bank.bankapi.customer.mapper.CustomerMapper;
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

import java.util.UUID;

@Service
public class CustomerService {


    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponseDTO create(CreateCustomerDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Creating customer for userId={}", userId);

        if (customerRepository.existsByUserId(user.getId())) {
            log.warn("Customer creation blocked: customer already exists for userId={}", userId);
            throw new ConflictException("Customer already exists for this user");
        }

        Customer customer = CustomerMapper.toEntity(dto, user.getId());
        Customer saved = customerRepository.save(customer);

        log.info("Customer created successfully: customerId={} for userId={}", saved.getId(), userId);

        return CustomerMapper.toResponse(saved);
    }

    public CustomerResponseDTO getById(UUID id, User user) {
        UUID userId = user.getId();

        log.info("Fetching customer by id={} requestedByUserId={}", id, userId);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found: customerId={} requestedByUserId={}", id, userId);
                    return new ResourceNotFoundException("Customer", id);
                });

        if (!customer.getUserId().equals(userId)) {
            log.warn("Forbidden customer access: customerId={} ownerUserId={} requestedByUserId={}",
                    id, customer.getUserId(), userId);
            throw new ForbiddenOperationException("You can't access this customer");
        }

        log.info("Customer fetched successfully: customerId={} requestedByUserId={}", id, userId);

        return CustomerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponseDTO update(UpdateCustomerDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Updating customer profile for userId={}", userId);

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for update: userId={}", userId);
                    return new ResourceNotFoundException("Customer", "userId", userId.toString());
                });

        customer.setFullName(dto.fullName());

        Customer saved = customerRepository.save(customer);

        log.info("Customer updated successfully: customerId={} userId={}", saved.getId(), userId);

        return CustomerMapper.toResponse(saved);
    }

    @Transactional
    public CustomerResponseDTO updateStatus(UUID customerId, UpdateCustomerStatusDTO dto, User user) {
        UUID userId = user.getId();

        log.info("Updating customer status: customerId={} requestedByUserId={} newStatus={}",
                customerId, userId, dto.status());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for status update: customerId={} requestedByUserId={}",
                            customerId, userId);
                    return new ResourceNotFoundException("Customer", customerId);
                });

        if (!customer.getUserId().equals(userId)) {
            log.warn("Forbidden status update: customerId={} ownerUserId={} requestedByUserId={}",
                    customerId, customer.getUserId(), userId);
            throw new ForbiddenOperationException("You can't update this customer status");
        }

        customer.setStatus(dto.status());
        Customer saved = customerRepository.save(customer);

        log.info("Customer status updated successfully: customerId={} userId={} status={}",
                saved.getId(), userId, saved.getStatus());

        return CustomerMapper.toResponse(saved);
    }
}