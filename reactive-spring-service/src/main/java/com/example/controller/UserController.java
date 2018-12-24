package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/users")
    public Flux<User> getUsers() {
        return userService.getAllUsers();

    }

    @GetMapping(value = "/users/{id}")
    public Mono<User> getUser(@PathVariable String id) {

        return userService.getUser(id);
    }

    //Making the output as stream so that Users will be pushed to the client based on it's availability
    @GetMapping(value = "/usersStream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> getUsersStream() {

        return userService.getAllUsersStream();

    }
}
