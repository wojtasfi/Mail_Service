package com.notification.search.service;

import com.google.common.base.Preconditions;
import com.notification.search.domain.MailDocument;
import com.notification.shared.MailSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailEventListener implements ApplicationListener<MailSentEvent> {

    private final MailSearchService mailSearchService;

    @Override
    public void onApplicationEvent(MailSentEvent mailSentEvent) {
        Preconditions.checkNotNull(mailSentEvent, "MailSentEvent cannot be null");

        mailSearchService.save(MailDocument.from(mailSentEvent));
    }
}
