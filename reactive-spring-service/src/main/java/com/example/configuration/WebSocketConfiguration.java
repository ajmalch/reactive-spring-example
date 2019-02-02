package com.example.configuration;

import com.example.event.UserCreatedEvent;
import com.example.event.UserCreatedEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class WebSocketConfiguration {

    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler webSocketHandler) {

        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/users", webSocketHandler));
                setOrder(10);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {

        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(
            ObjectMapper objectMapper,
            UserCreatedEventPublisher userCreatedEventPublisher) {

        Flux<UserCreatedEvent> publish =
                Flux.create(userCreatedEventPublisher).share();


        return webSocketSession -> {
            Flux<WebSocketMessage> messageFlux = publish
                    .map(userCreatedEvent ->
                    {

                        try {
                            return objectMapper.writeValueAsString(userCreatedEvent.getSource());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);

                        }
                    }).map(s -> {
                        log.info("Sending " + s);
                        return webSocketSession.textMessage(s);
                    });

            return webSocketSession.send(messageFlux);
        };

    }

}
