package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.*;
import com.register.wowlibre.domain.dto.client.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
public class CharactersDto implements Serializable {
    private List<CharacterDetailDto> characters;
    @JsonProperty("total_quantity")
    private Integer totalQuantity;
}
