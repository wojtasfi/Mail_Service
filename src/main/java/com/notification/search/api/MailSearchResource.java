package com.notification.search.api;

import com.notification.search.domain.dto.MailSearchHitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class MailSearchResource extends ResourceSupport {
    @Getter
    private UUID mailId;
    private String to;
    private String from;
    private String subject;
    private List<String> cc;
    private List<String> bcc;
    private String rawTextContent;
    private String htmlContent;
    private LocalDate date;

    public static MailSearchResource from(MailSearchHitDto dto) {
        return new MailSearchResource(
                dto.getId(),
                dto.getTo(),
                dto.getFrom(),
                dto.getSubject(),
                dto.getCc(),
                dto.getBcc(),
                dto.getRawTextContent(),
                dto.getHtmlContent(),
                dto.getDate()
        );
    }
}
