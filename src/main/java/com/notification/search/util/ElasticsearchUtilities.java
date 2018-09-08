package com.notification.search.util;

import com.notification.search.domain.MailDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.notification.search.service.MailSearchService.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchUtilities {

    public XContentBuilder buildMailSource(MailDocument mailDocument) throws IOException {
        return jsonBuilder()
                .startObject()
                .field(TO_FIELD, mailDocument.getTo())
                .field(FROM_FIELD, mailDocument.getFrom())
                .field(SUBJECT_FIELD, mailDocument.getSubject())
                .field(CC_FIELD, mailDocument.getCc())
                .field(BCC_FIELD, mailDocument.getBcc())
                .field(HTML_CONTENT_FIELD, mailDocument.getHtmlContent())
                .field(RAW_TEXT_CONTENT_FIELD, mailDocument.getRawTextContent())
                .endObject();
    }
}
