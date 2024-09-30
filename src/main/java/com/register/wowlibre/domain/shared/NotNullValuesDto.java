package com.register.wowlibre.domain.shared;

import lombok.Data;

import java.util.List;

@Data
public class NotNullValuesDto {
  private Integer numberOfInvalid;
  private List<String> valuesInvalid;
}
