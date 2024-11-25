package com.register.wowlibre.domain.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

public class PlanModel {
    public final Long id;
    public final String name;
    public final String description;
    public final String price;
    public final String frecuency;
    public final List<String> features;
    public final String button;
    public final Integer interest;
    @JsonProperty("month_payment_period")
    public final Integer monthPaymentPeriod;
    public final Double gold;

    public PlanModel(Long id, String name, String description, String price, String frecuency, List<String> features,
                     String button, Integer interest, Integer monthPaymentPeriod, Double gold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.frecuency = frecuency;
        this.features = features;
        this.button = button;
        this.interest = interest;
        this.monthPaymentPeriod = monthPaymentPeriod;
        this.gold = gold;
    }


}
