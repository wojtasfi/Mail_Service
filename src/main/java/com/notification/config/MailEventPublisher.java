package com.notification.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailEventPublisher implements ApplicationEventPublisher {

    private final ApplicationContext context;

    @Override
    public void publishEvent(ApplicationEvent event) {
        log.info("Publishing event: {}", event);
        context.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {
        log.info("Publishing event: {}", event);
        context.publishEvent(event);
    }
}
