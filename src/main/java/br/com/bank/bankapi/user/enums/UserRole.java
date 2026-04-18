package br.com.bank.bankapi.user.enums;

public enum UserRole {
    ROLE_CLIENT("client"),
    ROLE_MANAGER("manager"),
    ROLE_ADMIN("admin");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
