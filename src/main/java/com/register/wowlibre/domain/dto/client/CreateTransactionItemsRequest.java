package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import com.register.wowlibre.domain.model.*;
import lombok.*;

import java.util.*;


@AllArgsConstructor
@Getter
public class CreateTransactionItemsRequest {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("account_id")
    private Long accountId;
    private String reference;
    private List<ItemQuantityModel> items;
}
