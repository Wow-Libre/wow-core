package com.register.wowlibre.domain.dto.user_card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendCardRequestDto {

    @JsonProperty("card_code")
    private String cardCode;

    /** Correo del usuario destinatario; el backend lo busca por email. */
    @JsonProperty("to_user_email")
    private String toUserEmail;
}
