package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "guild_benefits_catalog")
public class GuildBenefitCatalogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(name = "subtitle")
    private String subTitle;
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "core_code")
    private String coreCode;
    private Integer quantity;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "external_url")
    private String externalUrl;
    private String language;
}
