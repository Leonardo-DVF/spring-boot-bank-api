package br.com.bank.bankapi.user.service;

import br.com.bank.bankapi.exception.core.ResourceNotFoundException;
import br.com.bank.bankapi.user.model.User;
import br.com.bank.bankapi.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void deactivateUser(UUID userId) {
        log.info("Deactivating user. userId={}", userId);

        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        if (!user.isActive()) {
            log.warn("User is already inactive. userId={}", userId);
            return;
        }

        user.setActive(false);
        repository.save(user);

        log.info("User deactivated successfully. userId={}", userId);
    }

    public void activateUser(UUID userId) {
        log.info("Activating user. userId={}", userId);

        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        if (user.isActive()) {
            log.warn("User is already active. userId={}", userId);
            return;
        }

        user.setActive(true);
        repository.save(user);

        log.info("User activated successfully. userId={}", userId);
    }
}