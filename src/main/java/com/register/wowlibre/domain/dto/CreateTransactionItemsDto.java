package com.register.wowlibre.domain.dto;

import com.register.wowlibre.domain.model.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Data
public class CreateTransactionItemsDto {
    @NotNull
    private Long realmId;
    @NotNull
    private Long userId;
    @NotNull
    private Long accountId;
    private String reference;
    @NotNull
    private List<ItemQuantityModel> items;
    private Double amount;
}
