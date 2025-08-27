package com.register.wowlibre.domain.dto.client;

import lombok.*;

@AllArgsConstructor
@Data
public class SendMailCommunicationRequest {
    private String email;
    private String subject;
    private String body;
    private String secret;
}
