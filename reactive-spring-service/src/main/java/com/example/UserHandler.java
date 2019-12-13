package com.example;

import com.example.model.User;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserHandler {

    private final UserService userService;

    Mono<ServerResponse> all(ServerRequest serverRequest) {

        log.debug("UserHandler.all");
        return defaultReadResponse(userService.getAllUsers());

    }

    Mono<ServerResponse> getById(ServerRequest serverRequest) {

        log.debug("UserHandler.getById");
        return defaultReadResponse(userService.getUser(id(serverRequest)));
    }

    Mono<ServerResponse> deleteById(ServerRequest serverRequest) {

        log.debug("UserHandler.deleteById");
        return defaultReadResponse(userService.delete(id(serverRequest)));
    }

    Mono<ServerResponse> updateById(ServerRequest serverRequest) {

        log.debug("UserHandler.updateById");

        final var updatedUser = serverRequest.bodyToMono(User.class)
                .flatMap(user -> userService.update(id(serverRequest), user.getName()));
        return defaultReadResponse(updatedUser);
    }

    Mono<ServerResponse> create(ServerRequest serverRequest) {

        log.debug("UserHandler.create");

        final var createdUser = serverRequest.bodyToMono(User.class)
                .flatMap(userService::create);
        return Mono.from(createdUser)
                .flatMap(user -> created(URI.create("/users/" + user.getId())).contentType(APPLICATION_JSON)
                        .build());

    }

    private Mono<ServerResponse> defaultReadResponse(Publisher<User> users) {
        return ok()
                .contentType(APPLICATION_JSON)
                .body(users, User.class);
    }

    private String id(ServerRequest serverRequest) {
        return serverRequest.pathVariable("id");
    }


}
