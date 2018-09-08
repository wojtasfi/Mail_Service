package com.notification.search;

import com.notification.search.domain.MailDocument;

import java.util.List;

public class MailDocumentBuilder {
    private String to;
    private String from;
    private String subject;
    private List<String> cc;
    private List<String> bcc;
    private String rawTextContent;
    private String htmlContent;

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

    public MailDocument createMailDocument() {
        return new MailDocument(to, from, subject, cc, bcc, rawTextContent, htmlContent);
    }
}