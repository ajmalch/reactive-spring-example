package com.example.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

@Component
public class UserCreatedEventPublisher implements
        ApplicationListener<UserCreatedEvent>,
        Consumer<FluxSink<UserCreatedEvent>> {

    private final Executor executor;

    private final BlockingQueue<UserCreatedEvent> queue = new LinkedBlockingDeque<>();

    public UserCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void accept(FluxSink<UserCreatedEvent> sink) {

        this.executor.execute(
                () -> {
                    while (true) {
                        try {
                            UserCreatedEvent event = queue.take();
                            sink.next(event);

                        } catch (InterruptedException e) {
                            ReflectionUtils.rethrowRuntimeException(e);

                        }
                    }
                }

        );
    }

    @Override
    public void onApplicationEvent(UserCreatedEvent userCreatedEvent) {

        this.queue.offer(userCreatedEvent);

    }
}
