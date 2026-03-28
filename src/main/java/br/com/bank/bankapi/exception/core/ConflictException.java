package br.com.bank.bankapi.exception.core;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
