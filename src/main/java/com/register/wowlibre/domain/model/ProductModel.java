package com.register.wowlibre.domain.model;

import lombok.*;

@Data
@Builder
public class ProductModel {
    private Long id;
    private String name;
    private String disclaimer;
    private boolean status;
    private String category;
    private Double price;
    private Integer discount;
    private Double discountPrice;
    private boolean usePoints;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String imgUrl;
    private String partner;
    private Long partnerId;
    private String referenceNumber;
    private String tax;
    private String returnTax;
    private Long pointsAmount;
    private String language;
}
