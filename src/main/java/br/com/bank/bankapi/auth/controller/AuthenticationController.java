package br.com.bank.bankapi.auth.controller;

import br.com.bank.bankapi.auth.dto.AuthenticationDTO;
import br.com.bank.bankapi.auth.dto.LoginResponseDTO;
import br.com.bank.bankapi.auth.dto.RegisterDTO;
import br.com.bank.bankapi.auth.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    // Authenticates the user and returns a JWT token
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data){
        log.info("Login requested. username={}", data.username());

        var response = userService.login(data);

        return ResponseEntity.ok(response);
    }

    // Registers a new user in the system
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data){
        log.info("User registration requested. username={}", data.username());

        userService.register(data);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}