package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.enums.BenefitPremiumType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class CreateBenefitPremiumDto {
  @NotNull
  private String img;
  @NotNull
  private String name;
  @NotNull
  private String description;
  @NotNull
  private String command;
  @NotNull
  private boolean sendItem;
  @NotNull
  private boolean reactivable;
  @NotNull
  private String btnText;
  @NotNull
  private BenefitPremiumType type;
  @NotNull
  private Long realmId;
  @NotNull
  private String language;
  private Double amount;
  private List<BenefitPremiumItemDto> items;
}
