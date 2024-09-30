package com.register.wowlibre.domain.model;

import java.util.*;

public class PlanModel {
    public final Long id;
    public final String name;
    public final String description;
    public final String price;
    public final String frecuency;
    public final List<String> features;
    public final String button;

    public PlanModel(Long id, String name, String description, String price, String frecuency, List<String> features,
                     String button) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.frecuency = frecuency;
        this.features = features;
        this.button = button;
    }


}
