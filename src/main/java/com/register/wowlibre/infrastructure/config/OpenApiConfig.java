package com.register.wowlibre.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wow Libre Core API")
                        .description("""
                                API REST para la gestión de usuarios y autenticación del sistema Wow Libre.

                                ## Características principales:
                                - Registro y autenticación de usuarios
                                - Gestión de perfiles de usuario
                                - Recuperación de contraseñas
                                - Validación de correos electrónicos
                                - Autenticación JWT

                                ## Autenticación:
                                Esta API utiliza JWT (JSON Web Tokens) para la autenticación.
                                Después de crear una cuenta o iniciar sesión, incluye el token JWT
                                en el header 'Authorization' con el prefijo 'Bearer '.

                                ## Códigos de respuesta:
                                - **201**: Recurso creado exitosamente
                                - **200**: Operación exitosa
                                - **400**: Solicitud incorrecta (datos de entrada inválidos)
                                - **401**: No autorizado (token JWT inválido o faltante)
                                - **403**: Prohibido (sin permisos suficientes)
                                - **409**: Conflicto (recurso ya existe)
                                - **500**: Error interno del servidor
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Wow Libre Development Team")
                                .email("dev@wowlibre.com")
                                .url("https://wowlibre.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8091/core")
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.wowlibre.com/core")
                                .description("Servidor de producción")));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-management")
                .pathsToMatch("/api/account/**")
                .build();
    }

}
