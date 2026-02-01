package com.register.wowlibre.domain.dto;

import java.util.*;


public record ProductCategoryModel(Long id, String name, String description, String disclaimer,
                                   List<ProductsDto> products) {
}
