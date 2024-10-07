package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.register.wowlibre.domain.dto.client.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class CharacterSocialDto {
    private List<CharacterSocialDetail> friends;
    @JsonProperty("total_quantity")
    private Integer totalQuantity;
}
