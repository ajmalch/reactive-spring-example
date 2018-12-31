package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Mono<User> getUser(String id) {

        return userRepository.findById(id);
    }

    public Flux<User> getAllUsers() {

        return userRepository.findAll();
    }


    //This is to simulate a one second delay
    public Flux<User> getAllUsersStream() {

        return Flux.interval(Duration.ofSeconds(1)).zipWith(userRepository.findAll()).map(Tuple2::getT2);

    }

    public Mono<User> create(Mono<User> user) {

        return user.doOnNext(userRepository::save);


    }
}
