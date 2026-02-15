package br.com.bank.bankapi.exception.core;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " not found. id=" + id);
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(resourceName + " not found. " + field + "=" + value);
    }
}
