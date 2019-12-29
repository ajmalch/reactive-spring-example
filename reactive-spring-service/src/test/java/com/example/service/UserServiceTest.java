package com.example.service;


import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.springframework.util.StringUtils.hasText;

@DataMongoTest
@Import(UserService.class)
@Slf4j
public class UserServiceTest {


    private final UserRepository userRepository;

    private final UserService userService;

    public UserServiceTest(@Autowired UserRepository userRepository, @Autowired UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @Test
    void getAllUsers() {

        log.debug("Testing UserService.getAllUsers");

        final var saved = userRepository.saveAll(
                Flux.just(
                        new User(null, "Ajmal")
                        , new User(null, "Shadiya")
                        , new User(null, "Haniya")));


        Flux<User> userFlux = userRepository.deleteAll()
                .thenMany(saved)
                .thenMany(userService.getAllUsers());

        Function<String, Predicate<User>> userPredicate = name -> user -> user.getName()
                .equals(name) && user.getId() != null;

        StepVerifier.create(userFlux)
                .expectNextMatches(userPredicate.apply("Ajmal"))
                .expectNextMatches(userPredicate.apply("Shadiya"))
                .expectNextMatches(userPredicate.apply("Haniya"))
                .verifyComplete();

    }

    @Test
    void create() {

        log.debug("Testing UserService.create");

        Mono<User> userMono = this.userService.create(new User(null, "Test"));

        StepVerifier
                .create(userMono)
                .expectNextMatches(saved -> hasText(saved.getId()))
                .verifyComplete();
    }

    @Test
    void update() {

        log.debug("Testing UserService.update");

        Mono<User> updated = userService.create(new User(null, "Ajmal"))
                .flatMap(user -> userService.update(user.getId(), "Maliha"))
                .doOnSuccess(user -> userService.getUser(user.getId()));

        StepVerifier.create(updated)
                .expectNextMatches(user -> user.getName()
                        .equals("Maliha"))
                .verifyComplete();

    }

    @Test
    void delete() {

        log.debug("Testing UserService.delete");

        final var user = userService.create(new User(null, "Ajmal"))
                .block();

        Mono<User> deletedUser = userService.delete(user.getId())
                .then(userService.getUser(user.getId()));

        //Verify that no user returned as we have already deleted the user
        StepVerifier.create(deletedUser)
                .verifyError(ResourceNotFoundException.class);

    }
}
