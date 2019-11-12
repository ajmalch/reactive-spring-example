package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //Making the output as stream so that Users will be pushed to the client based on it's availability
    @GetMapping(value = "/usersStream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> getUsersStream() {

        return userService.getAllUsersStream();

    }
}
