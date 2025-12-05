package com.register.wowlibre.domain.enums;

import lombok.*;

@Getter
public enum ChangePoints {
    GOLD("GOLD"),
    VOTING("VOTING"),
    POINTS("POINTS");

    private final String value;

    ChangePoints(String value) {
        this.value = value;
    }

    public ChangePoints getByValue(String value) {
        for (ChangePoints cp : ChangePoints.values()) {
            if (cp.getValue().equalsIgnoreCase(value)) {
                return cp;
            }
        }
        throw new IllegalArgumentException("Invalid ChangePoints value: " + value);
    }

}
