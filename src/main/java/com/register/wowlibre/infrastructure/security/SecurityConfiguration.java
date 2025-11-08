package com.register.wowlibre.infrastructure.security;

import com.register.wowlibre.domain.constant.InternalApiEndpoints;
import com.register.wowlibre.domain.constant.SwaggerEndpoints;
import com.register.wowlibre.domain.port.in.jwt.JwtPort;
import com.register.wowlibre.domain.security.UserDetailsServiceCustom;
import com.register.wowlibre.infrastructure.filter.AuthenticationFilter;
import com.register.wowlibre.infrastructure.filter.JwtAuthenticationFilter;
import com.register.wowlibre.infrastructure.util.Rol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceCustom userDetailsServiceCustom;
    private final JwtPort jwtPort;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                                 UserDetailsServiceCustom userDetailsServiceCustom,
                                 JwtPort jwtPort) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsServiceCustom = userDetailsServiceCustom;
        this.jwtPort = jwtPort;
    }

    /**
     * Combines all public endpoints (system endpoints, internal API, and Swagger) into a single array.
     */
    private String[] combinePublicEndpoints() {
        List<String> endpoints = new ArrayList<>();
        // System endpoints
        endpoints.add("/api/realm/key");
        endpoints.add("/actuator/health");
        endpoints.add("/api/voting/postback");
        endpoints.add("/api/voting");
        // Internal API
        endpoints.addAll(List.of(InternalApiEndpoints.getAllInternalApiEndpoints()));
        // Swagger
        endpoints.addAll(List.of(SwaggerEndpoints.SWAGGER_WHITELIST));
        return endpoints.toArray(new String[0]);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000",
                "http://127.0.0.1:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.OPTIONS.name(),
                HttpMethod.DELETE.name()));
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.AUTHORIZATION,
                HEADER_TRANSACTION_ID));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                        endpoints -> endpoints.requestMatchers("/api/auth/login").authenticated())
                .addFilterBefore(new AuthenticationFilter(authenticationManager(http), jwtPort),
                        UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    String[] publicEndpoints = combinePublicEndpoints();
                    request.requestMatchers(publicEndpoints).permitAll()
                            .requestMatchers("/api/realm/**", "/api/resources/create/faq",
                                    "/api/resources/delete/faq", "/api/transaction" +
                                            "/subscription-benefits",
                                    "/api/provider/**")
                            .hasAuthority(Rol.ADMIN.getName())
                            .requestMatchers(HttpMethod.POST, "/api/news",
                                    "/api/news/{newsId}/sections", "/api" +
                                            "/banners",
                                    "/api/voting/create")
                            .hasAuthority(Rol.ADMIN.getName())
                            .requestMatchers(HttpMethod.PUT, "/api/news/{id}", "/api/voting/{id}")
                            .hasAuthority(Rol.ADMIN.getName())
                            .requestMatchers(HttpMethod.DELETE, "/api/news/{id}",
                                    "/api/news/{newsId}/sections" +
                                            "/{sectionId}",
                                    "/api/banners/*", "/api/voting/{id}")
                            .hasAuthority(Rol.ADMIN.getName())
                            .anyRequest().authenticated();
                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationManager(authenticationManager(http)).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsServiceCustom)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }

}
