package com.example.event;

import com.example.model.User;
import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent extends ApplicationEvent {

    public UserCreatedEvent(User user) {
        super(user);
    }
}
