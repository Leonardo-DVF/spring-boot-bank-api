package br.com.bank.bankapi.service;

import br.com.bank.bankapi.dto.user.AuthenticationDTO;
import br.com.bank.bankapi.dto.user.LoginResponseDTO;
import br.com.bank.bankapi.dto.user.RegisterDTO;
import br.com.bank.bankapi.exception.InvalidCredentialsException;
import br.com.bank.bankapi.exception.UserAlreadyExistsException;
import br.com.bank.bankapi.exception.UserInactiveException;
import br.com.bank.bankapi.exception.UserNotFoundException;
import br.com.bank.bankapi.mapper.UserMapper;
import br.com.bank.bankapi.model.user.User;
import br.com.bank.bankapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import br.com.bank.bankapi.security.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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
        if (repository.findByUsername(data.username()) != null) {
            throw new UserAlreadyExistsException("Username already in use");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = UserMapper.toEntity(data, encryptedPassword);

        repository.save(newUser);
    }

    // Authenticates the user and returns a JWT token, throwing an exception if credentials are invalid
    public LoginResponseDTO login(AuthenticationDTO data) {

        UserDetails user = repository.findByUsername(data.username());

        if (user == null) {
          throw new UserNotFoundException("User not found with username: " + data.username());
        }

        if (!user.isEnabled()) {
            throw new UserInactiveException("User is inactive");
        }

        try {
            var usernamePassword =
                    new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());

            return new LoginResponseDTO(token);
        }catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}