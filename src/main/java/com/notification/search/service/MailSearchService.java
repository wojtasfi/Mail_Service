package com.notification.search.service;

import com.notification.search.api.SearchQuery;
import com.notification.search.domain.MailDocument;
import com.notification.search.domain.dto.MailSearchHitDto;
import org.springframework.data.domain.Pageable;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface MailSearchService {
    String TO_FIELD = "to";
    String FROM_FIELD = "from";
    String SUBJECT_FIELD = "subject";
    String CC_FIELD = "cc";
    String BCC_FIELD = "bcc";
    String HTML_CONTENT_FIELD = "html_content";
    String RAW_TEXT_CONTENT_FIELD = "raw_text";
    String DATE_FIELD = "date";
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    void save(MailDocument mailDocument);

    default void save(MailDocument... mailDocuments) {
        for (MailDocument mailDocument : mailDocuments) {
            save(mailDocument);
        }
    }

    List<MailSearchHitDto> search(SearchQuery searchQuery, Pageable pageable);
}
