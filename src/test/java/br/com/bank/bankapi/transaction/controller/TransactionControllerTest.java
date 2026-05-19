package br.com.bank.bankapi.transaction.controller;

import br.com.bank.bankapi.transaction.dto.TransactionResponseDTO;
import br.com.bank.bankapi.transaction.enums.TransactionType;
import br.com.bank.bankapi.transaction.service.TransactionService;
import br.com.bank.bankapi.user.enums.UserRole;
import br.com.bank.bankapi.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TransactionService transactionService;

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = user(UUID.randomUUID());
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransactionController(transactionService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(authenticatedUser))
                .build();
    }

    @Test
    void depositShouldReturnCreatedTransaction() throws Exception {
        TransactionResponseDTO response = transactionResponse(TransactionType.DEPOSIT);

        when(transactionService.deposit(any(), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DepositRequest(response.accountId(), new BigDecimal("150.00"), "Cash deposit"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(150.00));
    }

    @Test
    void withdrawShouldReturnCreatedTransaction() throws Exception {
        TransactionResponseDTO response = transactionResponse(TransactionType.WITHDRAW);

        when(transactionService.withdraw(any(), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WithdrawRequest(response.accountId(), new BigDecimal("150.00"), "ATM withdrawal"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("WITHDRAW"));
    }

    @Test
    void transferShouldReturnCreatedTransaction() throws Exception {
        TransactionResponseDTO response = transactionResponse(TransactionType.TRANSFER);

        when(transactionService.transfer(any(), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransferRequest(
                                response.accountId(),
                                "0001",
                                "765432-1",
                                new BigDecimal("150.00"),
                                "Transfer"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("TRANSFER"));
    }

    private TransactionResponseDTO transactionResponse(TransactionType type) {
        return new TransactionResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                type,
                new BigDecimal("150.00"),
                new BigDecimal("100.00"),
                new BigDecimal("250.00"),
                null,
                "Test transaction",
                Instant.parse("2026-05-19T12:00:00Z")
        );
    }

    private User user(UUID id) {
        User user = new User("client", "client@email.com", "encoded-password", UserRole.ROLE_CLIENT);
        setField(user, "id", id);
        return user;
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

    private record DepositRequest(UUID accountId, BigDecimal amount, String description) {
    }

    private record WithdrawRequest(UUID accountId, BigDecimal amount, String description) {
    }

    private record TransferRequest(
            UUID sourceAccountId,
            String destinationAgency,
            String destinationAccountNumber,
            BigDecimal amount,
            String description
    ) {
    }

    private record AuthenticationPrincipalResolver(User user) implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
        }

        @Override
        public Object resolveArgument(
                MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory
        ) {
            return user;
        }
    }
}
