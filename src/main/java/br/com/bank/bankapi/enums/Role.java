package br.com.bank.bankapi.enums;

public enum Role {
    ROLE_CLIENT("client"),
    ROLE_MANAGER("manager"),
    ROLE_ADMIN("admin");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
