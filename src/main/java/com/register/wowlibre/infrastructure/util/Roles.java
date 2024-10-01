package com.register.wowlibre.infrastructure.util;

import lombok.*;

@Getter
public enum Roles {
    ADMIN("ADMIN"),
    CLIENT("CLIENT"),
    SERVER("SERVER");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

}
