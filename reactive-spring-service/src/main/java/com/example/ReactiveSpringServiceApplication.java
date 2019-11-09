package com.example;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class ReactiveSpringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringServiceApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> addRoutes(UserService userService) {

        return route(GET("/users/{id}"),
                serverRequest ->
                        ok().body
                                (
                                        userService.getUser(
                                                serverRequest.pathVariable("id"))
                                        , User.class))
                .andRoute(POST("/users"),
                        serverRequest ->
                                serverRequest.bodyToMono(String.class)
                                        .map(name -> new User(null, name))
                                        .flatMap(userService::create)
                                        .flatMap(user ->
                                                created(
                                                        URI.create(
                                                                serverRequest.uri().toString() + "/" + user.getId()))
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .build())


                );

    }

}
