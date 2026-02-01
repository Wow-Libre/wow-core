package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.enums.BenefitPremiumType;
import java.util.List;
import lombok.Builder;

@Builder
public class BenefitsPremiumDto {
  public final Long id;
  public final String img;
  public final String name;
  public final String description;
  public final String command;
  public final Double amount;
  public final boolean sendItem;
  public final boolean reactivable;
  public final String btnText;
  public final BenefitPremiumType type;
  public final Long realmId;
  public final boolean status;
  public final List<BenefitPremiumItemDto> items;
  public final String language;
}
