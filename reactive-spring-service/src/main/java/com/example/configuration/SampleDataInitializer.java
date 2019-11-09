package com.example.configuration;

import com.example.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@Profile("demo")
@Slf4j
public class SampleDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ReactiveMongoRepository<User, String> userRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        log.debug("Initializing sample data");

        userRepository.deleteAll()
                .thenMany(
                        Flux.just("Ajmal", "Shadiya", "Maliha", "Aqila", "Frank", "Erwin", "Deepak", "John")
                                .map(name -> new User(null, name))
                                .flatMap(userRepository::save))
                .thenMany(userRepository.findAll())
                .subscribe(user -> System.out.println("User : " + user));

    }
}
