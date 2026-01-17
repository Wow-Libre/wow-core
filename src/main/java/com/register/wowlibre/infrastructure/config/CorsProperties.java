package com.register.wowlibre.infrastructure.config;

import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {
    private List<String> allowedOrigins = new ArrayList<>();
}
