package br.com.bank.bankapi.auth.service;

import br.com.bank.bankapi.user.enums.Role;
import br.com.bank.bankapi.user.model.User;
import br.com.bank.bankapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    AuthorizationService authorizationService;

    @Test
    void loadUserByUsernameUserFound() {
        User user = new User("leo", "leo@email.com", "hashed", Role.ROLE_CLIENT);
        when(repository.findByUsername("leo")).thenReturn(user);

        UserDetails result = authorizationService.loadUserByUsername("leo");

        assertNotNull(result);
        assertEquals("leo", result.getUsername());
        verify(repository).findByUsername("leo");
    }

    @Test
    void loadUserByUsernameUserNotFound() {
        when(repository.findByUsername("ghost")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> authorizationService.loadUserByUsername("ghost"));

        verify(repository).findByUsername("ghost");
    }
}