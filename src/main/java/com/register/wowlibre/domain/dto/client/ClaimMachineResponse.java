package com.register.wowlibre.domain.dto.client;

import lombok.*;

@Data
public class ClaimMachineResponse {
    private String logo;
    private String name;
    private boolean send;


    public ClaimMachineResponse(boolean send) {
        this.send = send;
    }
}
