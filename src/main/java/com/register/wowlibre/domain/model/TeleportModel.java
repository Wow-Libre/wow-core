package com.register.wowlibre.domain.model;

import lombok.*;

@Builder
public class TeleportModel {
    public final Long id;
    public final String imgUrl;
    public final float positionX;
    public final float positionY;
    public final float positionZ;
    public final float map;
    public final float orientation;
    public final float zona;
    public final float area;
}
