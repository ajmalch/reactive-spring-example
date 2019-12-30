package com.example.configuration;

import com.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketConfigurationTest {

    @LocalServerPort
    int randomServerPort;

    private final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

    private final WebClient webClient = WebClient.builder()
            .build();

    private User generateRandomUser() {
        return new User(UUID.randomUUID()
                .toString(), "name" + UUID.randomUUID());
    }

    @Test
    void testNotificationonUserCreation() throws Exception {

        int count = 10;
        AtomicInteger counter = new AtomicInteger();
        URI uri = URI.create("ws://localhost:" + randomServerPort + "/ws/users");

        webSocketClient.execute(uri, (WebSocketSession session) -> {
            Mono<WebSocketMessage> out = Mono.just(session.textMessage("trigger"));

            Flux<String> in = session.receive()
                    .map(WebSocketMessage::getPayloadAsText);

            return session.send(out)
                    .thenMany(in)
                    .doOnNext(s -> counter.getAndIncrement())
                    .then();
        })
                .subscribe();

        Flux.<User>generate(sink -> sink.next(generateRandomUser()))
                .take(count)
                .flatMap(this::write)
                .blockLast();

        Thread.sleep(1000);

        assertEquals(counter.get(), count);
    }

    private Publisher<User> write(User user) {

        return webClient.post()
                .uri("http://localhost:" + randomServerPort + "/users")
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(String.class)
                .thenReturn(user);
    }


}