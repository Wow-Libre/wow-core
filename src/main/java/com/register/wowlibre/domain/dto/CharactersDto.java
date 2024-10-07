package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.register.wowlibre.domain.dto.client.*;
import lombok.*;

import java.util.*;

@Data
public class CharactersDto {
    private List<CharacterDetailDto> characters;
    @JsonProperty("total_quantity")
    private Integer totalQuantity;
}
