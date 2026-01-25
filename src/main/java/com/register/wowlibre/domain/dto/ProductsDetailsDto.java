package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;

@Builder
@Data
public class ProductsDetailsDto {
    private List<ProductModel> products;
    private Integer totalProducts;
}
