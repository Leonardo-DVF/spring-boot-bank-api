package br.com.bank.bankapi.customer.mapper;

import br.com.bank.bankapi.customer.dto.CreateCustomerDTO;
import br.com.bank.bankapi.customer.dto.CustomerResponseDTO;
import br.com.bank.bankapi.customer.enums.CustomerStatus;
import br.com.bank.bankapi.customer.model.Customer;

import java.util.UUID;

public class CustomerMapper {

    private CustomerMapper() {}

    public static Customer toEntity(CreateCustomerDTO dto, UUID userId) {
        return new Customer(
                dto.fullName(),
                dto.document(),
                CustomerStatus.ACTIVE,
                userId
        );
    }

    public static CustomerResponseDTO toResponse(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getDocument(),
                customer.getStatus(),
                customer.getUserId(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
