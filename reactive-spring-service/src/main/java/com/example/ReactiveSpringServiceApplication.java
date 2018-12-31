package com.example;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class ReactiveSpringServiceApplication {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadUsers() {

        userRepository.deleteAll()
                .thenMany(
                        Flux.just("Ajmal", "Shadiya", "Maliha", "Aqila", "Frank", "Erwin", "Deepak", "John")
                                .map(name -> new User(null, name))
                                .flatMap(userRepository::save))
                .thenMany(userRepository.findAll())
                .subscribe(user -> System.out.println("User : " + user));
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
                                created(serverRequest.uri()).body(
                                        userService.create(
                                                serverRequest
                                                        .bodyToMono(String.class)
                                                        .map(name -> new User(null, name)))
                                        , User.class)


                );

    }

}
