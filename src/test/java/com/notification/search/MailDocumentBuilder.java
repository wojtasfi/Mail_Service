package com.notification.search;

import com.notification.search.domain.MailDocument;

import java.time.LocalDateTime;
import java.util.List;

import static com.notification.search.service.MailSearchService.DATE_FORMAT;

public class MailDocumentBuilder {
    private String to;
    private String from;
    private String subject;
    private List<String> cc;
    private List<String> bcc;
    private String rawTextContent;
    private String htmlContent;
    private String date;

    public MailDocumentBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public MailDocumentBuilder setFrom(String from) {
        this.from = from;
        return this;
    }

    public MailDocumentBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailDocumentBuilder setCc(List<String> cc) {
        this.cc = cc;
        return this;
    }

    public MailDocumentBuilder setBcc(List<String> bcc) {
        this.bcc = bcc;
        return this;
    }

    public MailDocumentBuilder setRawTextContent(String rawTextContent) {
        this.rawTextContent = rawTextContent;
        return this;
    }

    public MailDocumentBuilder setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    public MailDocumentBuilder setDate(String date) {
        this.date = date;
        return this;
    }

    public MailDocument createMailDocument() {
        if (date == null) {
            date = LocalDateTime.now().format(DATE_FORMAT);
        }
        return new MailDocument(to, from, subject, cc, bcc, rawTextContent, htmlContent, date);
    }
}