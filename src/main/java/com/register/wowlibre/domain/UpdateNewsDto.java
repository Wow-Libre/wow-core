package com.register.wowlibre.domain;

import jakarta.validation.constraints.*;

public class UpdateNewsDto {
    @NotNull
    public String title;
    @NotNull
    public String subTitle;
    @NotNull
    public String imgUrl;
    @NotNull
    public String author;
}
