package com.notification.sender;

import com.notification.sender.integration.api.FilePath;
import com.notification.sender.integration.api.FileString;
import com.notification.sender.integration.msg.SendMailMessage;

import java.util.*;

public class SendMailMessageBuilder {
    private String to;
    private String from;
    private String subject;
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private Locale locale;
    private String templateType;
    private Map<String, Object> templateParams = new HashMap<>();
    private List<FilePath> attachmentsPaths = new ArrayList<>();
    private List<FileString> attachmentsBase64Strings = new ArrayList<>();

    public SendMailMessageBuilder to(String to) {
        this.to = to;
        return this;
    }

    public SendMailMessageBuilder from(String from) {
        this.from = from;
        return this;
    }

    public SendMailMessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public SendMailMessageBuilder cc(List<String> cc) {
        this.cc = cc;
        return this;
    }

    public SendMailMessageBuilder bcc(List<String> bcc) {
        this.bcc = bcc;
        return this;
    }

    public SendMailMessageBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public SendMailMessageBuilder templateType(String templateType) {
        this.templateType = templateType;
        return this;
    }

    public SendMailMessageBuilder templateParams(Map<String, Object> templateParams) {
        this.templateParams = templateParams;
        return this;
    }

    public SendMailMessageBuilder attachmentsPaths(List<FilePath> attachmentsPaths) {
        this.attachmentsPaths = attachmentsPaths;
        return this;
    }

    public SendMailMessageBuilder attachmentsBase64Strings(List<FileString> attachmentsBase64Strings) {
        this.attachmentsBase64Strings = attachmentsBase64Strings;
        return this;
    }

    public SendMailMessage build() {
        return new SendMailMessage(to, from, subject, cc, bcc, locale, templateType, templateParams, attachmentsPaths, attachmentsBase64Strings);
    }
}