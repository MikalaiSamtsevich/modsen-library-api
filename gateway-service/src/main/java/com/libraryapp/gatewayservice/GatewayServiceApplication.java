package com.libraryapp.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(r -> r.path("/book-service-docs/v3/api-docs").and().method(HttpMethod.GET).uri("lb://book-service"))
                .route(r -> r.path("/library-service-docs/v3/api-docs").and().method(HttpMethod.GET).uri("lb://library-service"))
                .route(r -> r.path("/keycloak-auth-service-docs/v3/api-docs").and().method(HttpMethod.GET).uri("lb://keycloak-auth-service"))
                .build();
    }

}
