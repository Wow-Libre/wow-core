package com.register.wowlibre.domain.constant;

public class SwaggerEndpoints {
    public static final String[] SWAGGER_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/core/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };
}
