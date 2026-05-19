package br.com.bank.bankapi.account.controller;

import br.com.bank.bankapi.account.dto.AccountResponseDTO;
import br.com.bank.bankapi.account.enums.AccountStatus;
import br.com.bank.bankapi.account.enums.AccountType;
import br.com.bank.bankapi.account.service.AccountService;
import br.com.bank.bankapi.user.enums.UserRole;
import br.com.bank.bankapi.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AccountService accountService;

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = user(UUID.randomUUID());
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AccountController(accountService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalResolver(authenticatedUser))
                .build();
    }

    @Test
    void createShouldReturnCreatedAccount() throws Exception {
        AccountResponseDTO response = accountResponse(UUID.randomUUID(), UUID.randomUUID());

        when(accountService.create(any(), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateAccountRequest("0001", "123456-7", "CHECKING"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.agency").value("0001"))
                .andExpect(jsonPath("$.number").value("123456-7"));
    }

    @Test
    void listMyAccountsShouldReturnAccounts() throws Exception {
        AccountResponseDTO response = accountResponse(UUID.randomUUID(), UUID.randomUUID());

        when(accountService.listMyAccounts(any(User.class))).thenReturn(List.of(response));

        mockMvc.perform(get("/accounts/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(response.id().toString()));
    }

    @Test
    void updateStatusShouldReturnUpdatedAccount() throws Exception {
        AccountResponseDTO response = accountResponse(UUID.randomUUID(), UUID.randomUUID());

        when(accountService.updateStatus(any(UUID.class), any(), any(User.class))).thenReturn(response);

        mockMvc.perform(patch("/accounts/{id}/status", response.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateAccountStatusRequest("ACTIVE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void legacyDepositRouteShouldNotExist() throws Exception {
        UUID accountId = UUID.randomUUID();

        mockMvc.perform(post("/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void legacyWithdrawRouteShouldNotExist() throws Exception {
        UUID accountId = UUID.randomUUID();

        mockMvc.perform(post("/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isNotFound());
    }

    private AccountResponseDTO accountResponse(UUID id, UUID customerId) {
        Instant now = Instant.parse("2026-05-19T12:00:00Z");
        return new AccountResponseDTO(
                id,
                customerId,
                "0001",
                "123456-7",
                AccountType.CHECKING,
                AccountStatus.ACTIVE,
                new BigDecimal("100.00"),
                now,
                now
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

    private record CreateAccountRequest(String agency, String number, String type) {
    }

    private record UpdateAccountStatusRequest(String status) {
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
