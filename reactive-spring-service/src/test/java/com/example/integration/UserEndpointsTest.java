package com.example.integration;

import com.example.controller.UserEndpointConfiguration;
import com.example.controller.UserHandler;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest
@Import({UserEndpointConfiguration.class, UserHandler.class, UserService.class})
@Slf4j
class UserEndpointsTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    UserRepository userRepository;

    @Test
    public void getAll() {

        log.info("running " + this.getClass()
                .getName() + " getAll");

        Mockito.when(userRepository.findAll())
                .thenReturn(Flux.just(new User("1234", "Ajmal"), new User("1235", "Aqila")));

        webTestClient.get()
                .uri("/users")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.[0].id")
                .isEqualTo("1234")
                .jsonPath("$.[1].name")
                .isEqualTo("Aqila")
                .jsonPath("$.[2]")
                .doesNotExist();
    }

    @Test
    public void getById() {

        log.info("running " + this.getClass()
                .getName() + " getById");

        Mockito.when(userRepository.findById("123"))
                .thenReturn(Mono.just(new User("123", "Ajmal")));

        webTestClient.get()
                .uri("/users/123")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Ajmal");
    }

    @Test
    public void create() {

        log.info("running " + this.getClass()
                .getName() + " create");

        Mockito.when(userRepository.save(new User(null, "Ajmal")))
                .thenReturn(Mono.just(new User("123", "Ajmal")));

        webTestClient.post()
                .uri("/users")
                .body(Mono.just(new User(null, "Ajmal")), User.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectHeader()
                .valueEquals("Location", "/users/123")
                .expectBody()
                .isEmpty();
    }

    @Test
    public void updateById() {

        log.info("running " + this.getClass()
                .getName() + " updateById");

        User user = new User("123", "Ajmal");

        Mockito.when(userRepository.findById("123"))
                .thenReturn(Mono.just(user));

        Mockito.when(userRepository.save(user))
                .thenReturn(Mono.just(new User("123", "Shadiya")));

        webTestClient.put()
                .uri("/users/123")
                .body(Mono.just(new User(null, "Shadiya")), User.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Shadiya");
    }

    @Test
    public void deleteById() {

        log.info("running " + this.getClass()
                .getName() + " deleteById");

        User user = new User("123", "Ajmal");

        Mockito.when(userRepository.findById("123"))
                .thenReturn(Mono.just(user));
        Mockito.when(userRepository.deleteById("123"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/users/123")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Ajmal");
    }

}
