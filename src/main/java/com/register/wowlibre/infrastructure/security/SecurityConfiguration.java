package com.register.wowlibre.infrastructure.security;

import com.register.wowlibre.domain.port.in.jwt.*;
import com.register.wowlibre.domain.security.*;
import com.register.wowlibre.infrastructure.filter.*;
import com.register.wowlibre.infrastructure.filter.AuthenticationFilter;
import com.register.wowlibre.infrastructure.util.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.web.cors.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;

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

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000",
                                "http://127.0.0.1:3000", "http://localhost:3001", "http://127.0.0.1:3001"));
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
                                .authorizeHttpRequests(request -> request.requestMatchers(
                                                "/api/realm/key",
                                                "/actuator/health",
                                                "/api/voting/postback",
                                                "/api/voting",
                                                // INTERNAL API
                                                "/api/realm/advertising/language",
                                                "/api/account/create",
                                                "/api/account/password-recovery/request",
                                                "/api/account/password-recovery/confirm",
                                                "/api/account/search",
                                                "/api/resources/banners-home",
                                                "/api/resources/country",
                                                "/api/resources/widget-home",
                                                "/api/resources/faqs",
                                                "/api/resources/bank/plans",
                                                "/api/resources/benefit",
                                                "/api/resources/benefits-guild",
                                                "/api/resources/plan-acquisition",
                                                "/api/realm",
                                                "/api/guilds",
                                                "/api/guilds/{id}",
                                                "/api/realm/vdp",
                                                "/api/news",
                                                "/api/news/{id}",
                                                "/api/transaction/purchase",
                                                "/api/banners",
                                                // SWAGGER
                                                "/v2/api-docs",
                                                "/swagger-resources",
                                                "/swagger-resources/**",
                                                "/configuration/ui",
                                                "/configuration/security",
                                                "/swagger-ui.html",
                                                "/webjars/**",
                                                "/core/v3/api-docs",
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**")
                                                .permitAll()
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
                                                .anyRequest().authenticated())
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
