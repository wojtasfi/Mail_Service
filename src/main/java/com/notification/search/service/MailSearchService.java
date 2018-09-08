package com.notification.search.service;

import com.notification.search.domain.MailDocument;
import com.notification.search.util.ElasticsearchUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSearchService {
    //todo extract interface in future

    public static final String TO_FIELD = "to";
    public static final String FROM_FIELD = "from";
    public static final String SUBJECT_FIELD = "subject";
    public static final String CC_FIELD = "cc";
    public static final String BCC_FIELD = "bcc";
    public static final String HTML_CONTENT_FIELD = "html_content";
    public static final String RAW_TEXT_CONTENT_FIELD = "raw_text";
    private final Client client;
    private final ElasticsearchUtilities esUtils;
    private final String MAIL_INDEX = "mail";

    public void save(MailDocument mailDocument) {
        String mailId = UUID.randomUUID().toString();
        try {
            client.prepareIndex()
                    .setIndex(MAIL_INDEX)
                    .setType(MAIL_INDEX)
                    .setId(mailId)
                    .setSource(esUtils.buildMailSource(mailDocument))
                    .execute();
        } catch (Exception e) {
            //todo put it on kafka with retry policy or send email to admin
            log.error("Failed to index MailDocument with id {}", mailId, e);
        }
    }
}
