package org.nurim.nurim.domain.entity;

import org.springframework.security.core.GrantedAuthority;

public enum MemberRole implements GrantedAuthority {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private String role;

    MemberRole(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }

    @Override
    public String getAuthority() {
        return null;
    }
}
