package com.register.wowlibre.infrastructure.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@Data
public class Configurations {
    @Value("${application.host.wow-libre}")
    private String hostWowLibre;
}
