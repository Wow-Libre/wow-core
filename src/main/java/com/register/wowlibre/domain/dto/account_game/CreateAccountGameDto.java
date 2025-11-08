package com.register.wowlibre.domain.dto.account_game;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Data
public class CreateAccountGameDto {
    @NotNull
    @Length(min = 5, max = 20)
    private String username;
    @JsonProperty("game_mail")
    private String gameMail;
    @NotNull
    @Length(min = 3, max = 30)
    private String password;
    @NotNull
    private String realmName;
    @NotNull
    private Integer expansion;
}
