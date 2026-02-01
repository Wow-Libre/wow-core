package com.register.wowlibre.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlanDetailDto {
  private Long id;
  private String name;
  private Double price;
  private String priceTitle;
  private String description;
  private Double discountedPrice;
  private Integer discount;
  private boolean status;
  private String currency;
  private String frequencyType;
  private Integer frequencyValue;
  private Integer freeTrialDays;
  private String tax;
  private String returnTax;
  private List<String> features;
  private String language;
}
