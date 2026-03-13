package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanAdminRequestDto {
  private Long id;

  @NotNull(message = "name is required")
  @Size(min = 1, max = 255)
  private String name;

  @NotNull(message = "price is required")
  @DecimalMin(value = "0", inclusive = true)
  private Double price;

  private String currency;
  private Integer discount;
  private Boolean status;

  @JsonProperty("frequency_type")
  private String frequencyType;

  @JsonProperty("frequency_value")
  private Integer frequencyValue;

  private List<String> features;
}
