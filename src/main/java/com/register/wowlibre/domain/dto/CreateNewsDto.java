package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;

public class CreateNewsDto {
    @NotNull
    public String title;
    @NotNull
    public String subTitle;
    @NotNull
    public String imgUrl;
    @NotNull
    public String author;
}
