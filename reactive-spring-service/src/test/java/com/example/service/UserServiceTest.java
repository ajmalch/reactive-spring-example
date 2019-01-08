package com.example.service;


import com.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(UserService.class)
@Slf4j
public class UserServiceTest {

    private final UserService userService;

    public UserServiceTest(@Autowired UserService userService) {
        this.userService = userService;
    }

    @Test
    public void create() {

        log.info("Testing UserService.create");

        Mono<User> userMono = this.userService.create(new User(null, "Test"));

        StepVerifier
                .create(userMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
                .verifyComplete();
    }
}
