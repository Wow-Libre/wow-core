package com.register.wowlibre.domain.dto;

import lombok.*;

@Builder
@Data
public class MachineDto {
    private String logo;
    private String name;
    private String type;
    private boolean winner;
    private String message;
}
