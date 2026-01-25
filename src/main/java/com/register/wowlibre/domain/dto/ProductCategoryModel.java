package com.register.wowlibre.domain.dto;

import lombok.*;

import java.util.*;

@AllArgsConstructor
public class ProductCategoryModel {
    public Long id;
    public String name;
    public String description;
    public String disclaimer;
    public List<ProductsDto> products;
}
