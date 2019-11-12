package com.example.service;

import com.example.event.UserCreatedEvent;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher publisher;

    public Mono<User> getUser(@NotBlank String id) {

        log.debug("UserService.getUser");

        return userRepository.findById(id);
    }

    public Flux<User> getAllUsers() {

        log.debug("UserService.getAllUsers");

        return userRepository.findAll();
    }


    //This is to simulate a one second delay
    public Flux<User> getAllUsersStream() {

        log.debug("UserService.getAllUsersStream");

        return Flux.interval(Duration.ofSeconds(1)).zipWith(userRepository.findAll()).map(Tuple2::getT2);

    }

    public Mono<User> create(@Valid User user) {

        log.debug("UserService.create");

        return userRepository.save(user).doOnSuccess(u -> publisher.publishEvent(new UserCreatedEvent(u)));

    }

    public Mono<User> update(String id, @NotBlank String name) {

        log.debug("UserService.update");

        return userRepository.findById(id)
                .map(user -> new User(user.getId(), name))
                .flatMap(userRepository::save);
    }

    public Mono<User> delete(String id) {

        log.debug("UserService.delete");

        return userRepository.findById(id)
                .flatMap(user -> userRepository.deleteById(user.getId())
                        .thenReturn(user));
    }

}
