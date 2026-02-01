package com.register.wowlibre.domain.dto;

import lombok.*;

@Getter
@Builder
public class ProductDiscountsDto {
    private Long id;
    private String name;
    private String disclaimer;
    private String category;
    private Double price;
    private Double discountPrice;
    private Integer discount;
    private boolean usePoints;
    private Long serverId;
    private String description;
    private String imgUrl;
    private String partner;
    private String referenceNumber;
}
