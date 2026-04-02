package com.register.wowlibre.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Data
public class RealmCreateDto {
    @NotNull
    @Length(min = 5, max = 40)
    private String name;
    @NotNull
    @Length(min = 5, max = 40)
    private String emulator;
    @NotNull
    private Integer expansion;
    @NotNull
    @Length(min = 5, max = 50)
    private String webSite;
    @NotNull
    @Length(min = 5, max = 50)
    private String host;
    @NotNull
    @Length(min = 5, max = 30)
    private String password;
    @NotNull
    @Length(min = 5, max = 80)
    private String realmlist;
    @NotNull
    @Length(min = 5, max = 30)
    private String type;
    @NotNull
    private Long realmId;

    /**
     * Si es false, el reino no se ofrece en el alta de cuenta de juego (sí puede usarse para vinculación u otros flujos).
     * Null se interpreta como true.
     */
    @JsonProperty("show_in_game_registration")
    private Boolean showInGameRegistration;
}
