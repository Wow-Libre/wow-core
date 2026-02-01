package com.register.wowlibre.domain.dto;

import lombok.*;

@Getter
@Builder
public class ProductsDto {
    private Long id;
    private String name;
    private String disclaimer;
    private String category;
    private Double price;
    private Integer discount;
    private Double discountPrice;
    private boolean usePoints;
    private String description;
    private String imgUrl;
    private String partner;
    private String referenceNumber;
}
