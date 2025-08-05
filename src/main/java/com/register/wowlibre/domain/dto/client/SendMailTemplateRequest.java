package com.register.wowlibre.domain.dto.client;

import lombok.*;

import java.util.*;

@Builder
@Data
public class SendMailTemplateRequest {
    private String email;
    private String subject;
    private String secret;
    private Long templateId;
    private Map<String, String> body;
}
