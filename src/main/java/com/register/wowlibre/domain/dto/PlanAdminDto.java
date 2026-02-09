package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanAdminDto {
  private Long id;
  private String name;
  private Double price;
  private String currency;
  private Integer discount;

  @JsonProperty("discounted_price")
  private Double discountedPrice;

  private Boolean status;

  @JsonProperty("frequency_type")
  private String frequencyType;

  @JsonProperty("frequency_value")
  private Integer frequencyValue;
}
