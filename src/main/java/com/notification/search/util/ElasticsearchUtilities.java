package com.notification.search.util;

import com.notification.search.domain.MailDocument;
import com.notification.search.domain.dto.MailSearchHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.notification.search.service.MailSearchServiceImpl.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchUtilities {

//    public XContentBuilder buildMailSource(MailDocument mailDocument) throws IOException {
//        return jsonBuilder()
//                .startObject()
//                .field(TO_FIELD, mailDocument.getTo())
//                .field(FROM_FIELD, mailDocument.getFrom())
//                .field(SUBJECT_FIELD, mailDocument.getSubject())
//                .field(CC_FIELD, mailDocument.getCc())
//                .field(BCC_FIELD, mailDocument.getBcc())
//                .field(HTML_CONTENT_FIELD, mailDocument.getHtmlContent())
//                .field(RAW_TEXT_CONTENT_FIELD, mailDocument.getRawTextContent())
//                .timeField(DATE_FIELD, mailDocument.getDate())
//                .endObject();
//    }

    public Map<String, Object> buildMailSource(MailDocument mailDocument) {
        Map<String, Object> map = new HashMap<>();
        map.put(TO_FIELD, mailDocument.getTo());
        map.put(FROM_FIELD, mailDocument.getFrom());
        map.put(SUBJECT_FIELD, mailDocument.getSubject());
        map.put(CC_FIELD, mailDocument.getCc());
        map.put(BCC_FIELD, mailDocument.getBcc());
        map.put(HTML_CONTENT_FIELD, mailDocument.getHtmlContent());
        map.put(RAW_TEXT_CONTENT_FIELD, mailDocument.getRawTextContent());
        map.put(DATE_FIELD, mailDocument.getDate());
        return map;
    }

    public List<MailSearchHitDto> convertHitsToMailSearchHitDto(SearchResponse response) {
        List<MailSearchHitDto> list = new ArrayList<>();
        response.getHits().iterator()
                .forEachRemaining(documentFields -> list.add(convertHitToMailSearchHitDto(documentFields)));
        return list;
    }
//
//    public int getNow() {
//        LocalDateTime time = LocalDateTime.now();
//
//        String year = String.valueOf(time.getYear());
//        String month = String.valueOf(time.getMonth());
//        String day = String.valueOf(time.getDayOfMonth());
//        String hour = String.valueOf(time.getHour());
//        String minute = String.valueOf(time.getYear());
//        String second = String.valueOf(time.getYear());
//
//    }

    private MailSearchHitDto convertHitToMailSearchHitDto(SearchHit hit) {

        Map<String, Object> map = hit.getSourceAsMap();
        MailSearchHitDto dto = new MailSearchHitDto();
        dto.setId(UUID.fromString(hit.getId()));
        dto.setTo(toStringOrNull(map.get(TO_FIELD)));
        dto.setFrom(toStringOrNull(map.get(FROM_FIELD)));
        dto.setSubject(toStringOrNull(map.get(SUBJECT_FIELD)));
        dto.setCc(toListOrNull(map.get(CC_FIELD)));
        dto.setBcc(toListOrNull(map.get(BCC_FIELD)));
        dto.setHtmlContent(toStringOrNull(map.get(HTML_CONTENT_FIELD)));
        dto.setRawTextContent(toStringOrNull(map.get(RAW_TEXT_CONTENT_FIELD)));
        dto.setDate(toLocalDateOrNull(map.get(DATE_FIELD)));
        return dto;
    }

    private LocalDateTime toLocalDateOrNull(Object object) {
        if (object == null) {
            return null;
        }
        //LocalDateTime.parse(object.toString)- it also works but I`ll have to lose DATE_FORMAT
        return LocalDateTime.parse(object.toString(), DATE_FORMAT);
    }

    private List<String> toListOrNull(Object object) {
        if (object == null) {
            return null;
        }
        return (List<String>) object;
    }

    private String toStringOrNull(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }
}
