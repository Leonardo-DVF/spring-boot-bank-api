package br.com.bank.bankapi.customer.service;

import br.com.bank.bankapi.customer.dto.CreateCustomerDTO;
import br.com.bank.bankapi.customer.dto.UpdateCustomerStatusDTO;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void createShouldNormalizeDocumentBeforeSaving() {
        User user = user(UUID.randomUUID());
        CreateCustomerDTO dto = new CreateCustomerDTO("Leonardo Ferreira", "218.720.100-75");
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        when(customerRepository.existsByUserId(user.getId())).thenReturn(false);
        when(customerRepository.existsByDocument("21872010075")).thenReturn(false);
        when(customerRepository.save(customerCaptor.capture())).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            setField(customer, "id", UUID.randomUUID());
            return customer;
        });

        customerService.create(dto, user);

        assertEquals("21872010075", customerCaptor.getValue().getDocument());
        assertEquals(user.getId(), customerCaptor.getValue().getUserId());
    }

    @Test
    void createShouldRejectUserThatAlreadyHasCustomer() {
        User user = user(UUID.randomUUID());
        CreateCustomerDTO dto = new CreateCustomerDTO("Leonardo Ferreira", "21872010075");

        when(customerRepository.existsByUserId(user.getId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> customerService.create(dto, user));

        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateStatusShouldRejectCustomerFromAnotherUser() {
        User user = user(UUID.randomUUID());
        Customer customer = customer(UUID.randomUUID(), UUID.randomUUID());
        UpdateCustomerStatusDTO dto = new UpdateCustomerStatusDTO(CustomerStatus.BLOCKED);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        assertThrows(ForbiddenOperationException.class, () -> customerService.updateStatus(customer.getId(), dto, user));

        verify(customerRepository, never()).save(customer);
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
