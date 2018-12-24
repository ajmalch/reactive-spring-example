package com.example;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;

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

}
