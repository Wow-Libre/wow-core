package com.register.wowlibre.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class TeleportDto {
    @NotNull
    private String imgUrl;
    @NotNull
    private float positionX;
    @NotNull
    private float positionY;
    @NotNull
    private float positionZ;
    @NotNull
    private float map;
    @NotNull
    private float orientation;
    @NotNull
    private float zona;
    @NotNull
    private float area;
}
