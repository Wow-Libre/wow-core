package com.register.wowlibre.infrastructure.entities.transactions;

import com.register.wowlibre.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Entity
@Table(name = "benefit_premiums", schema = "platform")
public class BenefitPremiumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String img;
    private String name;
    private String description;
    private String command;
    @Column(name = "send_item")
    private boolean sendItem;
    private boolean reactivable;
    @Column(name = "btn_text")
    private String btnText;
    @Enumerated(EnumType.STRING)
    private BenefitPremiumType type;
    @Column(name = "realm_id")
    private Long realmId;
    private String language;
    private Double amount;
    private boolean status;

    @OneToMany(mappedBy = "benefitPremium", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BenefitPremiumItemEntity> items;
}
