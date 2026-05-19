package br.com.bank.bankapi.auth.service;

import br.com.bank.bankapi.user.model.User;
import br.com.bank.bankapi.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    private final UserRepository repository;

    public AuthorizationService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User loadUserByUsername(String username) {
        log.debug("Loading user by username. username={}", username);

        User user = (User) repository.findByUsername(username);

        if (user == null) {
            log.warn("User not found during authentication lookup. username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return user;
    }
}