package com.notification.search.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class MailSearchHitDto {
    private UUID id;
    private String to;
    private String from;
    private String subject;
    private List<String> cc;
    private List<String> bcc;
    private String rawTextContent;
    private String htmlContent;
    private LocalDate date;
}
