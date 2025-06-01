package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;

public class CreateNewsSectionDto {
    @NotNull
    public String title;
    @NotNull
    public String content;
    @NotNull
    public String imgUrl;
}
