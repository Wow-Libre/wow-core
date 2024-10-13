package com.register.wowlibre.domain.model;

public class BenefitGuildModel {
    public final Long id;
    public final Long benefitId;
    public final boolean status;

    public BenefitGuildModel(Long id, Long benefitId, boolean status) {
        this.id = id;
        this.benefitId = benefitId;
        this.status = status;
    }
}
