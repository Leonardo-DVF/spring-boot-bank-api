package br.com.bank.bankapi.exception.core;

public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public ForbiddenOperationException(String resourceName, String operation) {
        super("Forbidden operation: " + operation + " on " + resourceName);
    }
}