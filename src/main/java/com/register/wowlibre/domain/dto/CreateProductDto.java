package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Data
public class CreateProductDto {
    @NotNull
    private String name;
    @NotNull
    private Long productCategoryId;
    @NotNull
    private String disclaimer;
    @NotNull
    private Double price;
    @NotNull
    private Integer discount;
    @NotNull
    private String description;
    @NotNull
    private String imageUrl;
    @NotNull
    private Long realmId;
    @NotNull
    private String realmName;
    @NotNull
    private String language;
    @NotNull
    private String tax;
    @NotNull
    private String returnTax;
    @NotNull
    private Long creditPointsValue;
    @NotNull
    private Boolean creditPointsEnabled;
    private List<ProductDetailsDto> details;
    @NotNull
    private List<String> packages;
}
