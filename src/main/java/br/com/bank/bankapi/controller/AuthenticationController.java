package br.com.bank.bankapi.controller;

import br.com.bank.bankapi.dto.user.AuthenticationDTO;
import br.com.bank.bankapi.dto.user.LoginResponseDTO;
import br.com.bank.bankapi.dto.user.RegisterDTO;
import br.com.bank.bankapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    // Authenticates the user and returns a JWT token
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data){
        var response = userService.login(data);
        return ResponseEntity.ok(response);
    }

    // Registers a new user in the system
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data){
        userService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}