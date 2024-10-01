package com.libraryapp.libraryservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Bean

    public OpenAPI customOpenAPI(@Value("${openapi.service.title}") String serviceTitle,
                                 @Value("${openapi.service.version}") String serviceVersion,
                                 @Value("${openapi.service.url}") String url) {
        return new OpenAPI()
                .servers(List.of(new Server().url(url)))
                .info(new Info()
                        .title(serviceTitle).version(serviceVersion)
                        .description("Book Status Service API for managing the status of books in the library. " +
                                "When a book is created or deleted in the Book Service, its id is sent to this service via Kafka for further processing. " +
                                "To access GET requests, the USER role is required, while the ADMIN role is needed for creating or modifying book statuses."))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                )
                ;

    }
}