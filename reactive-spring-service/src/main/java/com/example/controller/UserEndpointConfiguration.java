package com.example.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserEndpointConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(UserHandler userHandler) {

        return route(GET("/users"), userHandler::all)
                .andRoute(GET("/users/{id}"), userHandler::getById)
                .andRoute(DELETE("/users/{id}"), userHandler::deleteById)
                .andRoute(PUT("/users/{id}"), userHandler::updateById)
                .andRoute(POST("/users"), userHandler::create);

    }

}
