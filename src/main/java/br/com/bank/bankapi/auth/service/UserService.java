package br.com.bank.bankapi.auth.service;

import br.com.bank.bankapi.auth.dto.AuthenticationDTO;
import br.com.bank.bankapi.auth.dto.LoginResponseDTO;
import br.com.bank.bankapi.auth.dto.RegisterDTO;
import br.com.bank.bankapi.auth.exception.InvalidCredentialsException;
import br.com.bank.bankapi.user.exception.UserAlreadyExistsException;
import br.com.bank.bankapi.user.exception.UserInactiveException;
import br.com.bank.bankapi.user.exception.UserNotFoundException;
import br.com.bank.bankapi.auth.mapper.UserMapper;
import br.com.bank.bankapi.user.model.User;
import br.com.bank.bankapi.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import br.com.bank.bankapi.auth.security.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    // Registers a new user ensuring a unique username and encrypted password
    public void register(RegisterDTO data) {
        log.info("Starting user registration. username={}", data.username());

        if (repository.findByUsername(data.username()) != null) {
            log.warn("Registration failed: username already exists. username={}", data.username());
            throw new UserAlreadyExistsException("Username already in use");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = UserMapper.toEntity(data, encryptedPassword);

        repository.save(newUser);

        log.info("User registered successfully. username={}", data.username());
    }

    // Authenticates the user and returns a JWT token, throwing an exception if credentials are invalid
    public LoginResponseDTO login(AuthenticationDTO data) {
        log.info("Starting authentication. username={}", data.username());

        UserDetails user = repository.findByUsername(data.username());

        if (user == null) {
            log.warn("Authentication failed: user not found. username={}", data.username());
            throw new UserNotFoundException("User not found with username: " + data.username());
        }

        if (!user.isEnabled()) {
            log.warn("Authentication failed: user inactive. username={}", data.username());
            throw new UserInactiveException("User is inactive");
        }

        try {
            var usernamePassword =
                    new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());

            log.info("User authenticated successfully. username={}", data.username());
            return new LoginResponseDTO(token);

        }catch (BadCredentialsException e) {
            log.warn("Authentication failed: invalid credentials. username={}", data.username());
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}