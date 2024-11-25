package com.register.wowlibre.domain.dto.client;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@Data
public class CharacterSocialResponse {
    private List<CharacterSocialDetail> friends;
    @JsonProperty("total_quantity")
    private Integer totalQuantity;
}
