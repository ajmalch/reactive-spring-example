package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveSpringServiceApplication {

    public ReactiveSpringServiceApplication(UserRepository uR) {

        this.userRepository = uR;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveSpringServiceApplication.class, args);
    }

    private UserRepository userRepository;

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


@RestController
class UserController {

    private final UserRepository userRepository;

    UserController(UserRepository uR) {
        this.userRepository = uR;
    }

    @GetMapping("/users")
    public Flux<User> getUsers() {
        return userRepository.findAll();

    }
}

interface UserRepository extends ReactiveMongoRepository<User, String> {

}


@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class User {

    @Id
    private String id;

    private String name;


}
