package com.register.wowlibre.domain.model;

public class RolModel {
    public final Long id;
    public final String name;
    public final boolean status;

    public RolModel(Long id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}
