package org.nurim.nurim.domain.entity;

public enum MemberRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private String role;

    MemberRole(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }

}
