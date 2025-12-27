package br.com.bank.bankapi.service;

import br.com.bank.bankapi.dto.user.AuthenticationDTO;
import br.com.bank.bankapi.dto.user.LoginResponseDTO;
import br.com.bank.bankapi.dto.user.RegisterDTO;
import br.com.bank.bankapi.enums.Role;
import br.com.bank.bankapi.exception.InvalidCredentialsException;
import br.com.bank.bankapi.exception.UserAlreadyExistsException;
import br.com.bank.bankapi.exception.UserInactiveException;
import br.com.bank.bankapi.exception.UserNotFoundException;
import br.com.bank.bankapi.model.user.User;
import br.com.bank.bankapi.repository.UserRepository;
import br.com.bank.bankapi.security.TokenService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    TokenService tokenService;

    @InjectMocks UserService userService;

    // Should register a user when the username does not exist
    @Test
    public void registerUser() {
        RegisterDTO dto = new RegisterDTO("leo", "leo@email.com", "12345678", Role.ROLE_CLIENT);

        when(repository.findByUsername("leo")).thenReturn(null);
        when(passwordEncoder.encode("12345678")).thenReturn("hashed");

        userService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("leo", saved.getUsername());
        assertEquals("leo@email.com", saved.getEmail());
        assertEquals("hashed", saved.getPassword());
        assertEquals(Role.ROLE_CLIENT, saved.getRole());

        verify(passwordEncoder).encode("12345678");
        verify(repository).findByUsername("leo");
    }

    // Should return a token when login is valid
    @Test
    public void loginUser() {
        AuthenticationDTO dto = new AuthenticationDTO("leo", "12345678");

        User userFromRepo = new User("leo", "leo@email.com", "hashed", Role.ROLE_CLIENT);
        when(repository.findByUsername("leo")).thenReturn(userFromRepo);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        User principal = new User("leo", "leo@email.com", "hashed", Role.ROLE_CLIENT);
        when(auth.getPrincipal()).thenReturn(principal);

        when(tokenService.generateToken(principal)).thenReturn("fake-jwt-token");

        LoginResponseDTO response = userService.login(dto);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());

        verify(repository).findByUsername("leo");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(principal);
    }

    // Should throw an exception when the username is already taken
    @Test
    public void registerUserAlreadyExists() {
        RegisterDTO dto = new RegisterDTO("leo", "leo@email.com", "12345678", Role.ROLE_CLIENT);

        when(repository.findByUsername("leo")).thenReturn(new User());

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(dto));

        verify(repository).findByUsername("leo");

        verify(repository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    // Should throw an exception when the user is not found during login
    @Test
    public void loginUserNotFound() {
        AuthenticationDTO dto = new AuthenticationDTO("naoexiste", "12345678");

        when(repository.findByUsername("naoexiste")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.login(dto));

        verify(repository).findByUsername("naoexiste");

        verify(authenticationManager, never()).authenticate(any());
        verify(tokenService, never()).generateToken(any());
    }

    // Should throw an exception when the user is inactive during login
    @Test
    public void loginUserInactive() {
        AuthenticationDTO dto = new AuthenticationDTO("leo", "12345678");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(false);
        when(repository.findByUsername("leo")).thenReturn(userDetails);

        assertThrows(UserInactiveException.class, () -> userService.login(dto));

        verify(repository).findByUsername("leo");

        verify(authenticationManager, never()).authenticate(any());
        verify(tokenService, never()).generateToken(any());
    }

    // Should throw an exception when login credentials are invalid
    @Test
    public void loginUserInvalidCredentials() {
        AuthenticationDTO dto = new AuthenticationDTO("leo", "senhaerrada");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(true);
        when(repository.findByUsername("leo")).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> userService.login(dto));

        verify(repository).findByUsername("leo");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(tokenService, never()).generateToken(any());
    }
}