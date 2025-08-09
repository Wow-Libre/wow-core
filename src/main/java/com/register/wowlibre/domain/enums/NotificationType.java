package com.register.wowlibre.domain.enums;

import java.util.*;

public enum NotificationType {
    MAILS,
    METRICS;

    public static NotificationType fromName(String name) {
        return Arrays.stream(NotificationType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
