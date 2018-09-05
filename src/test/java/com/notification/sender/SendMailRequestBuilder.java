package com.notification.sender;

import com.notification.sender.api.FilePath;
import com.notification.sender.api.FileString;
import com.notification.sender.api.SendMailRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendMailRequestBuilder {
    private String to;
    private String from;
    private String subject;
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private String templateType;
    private Map<String, Object> templateParams = new HashMap<>();
    private List<FilePath> attachmentsPaths = new ArrayList<>();
    private List<FileString> attachmentsBase64Strings = new ArrayList<>();

    public SendMailRequestBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public SendMailRequestBuilder setFrom(String from) {
        this.from = from;
        return this;
    }

    public SendMailRequestBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public SendMailRequestBuilder setCc(List<String> cc) {
        this.cc = cc;
        return this;
    }

    public SendMailRequestBuilder setBcc(List<String> bcc) {
        this.bcc = bcc;
        return this;
    }

    public SendMailRequestBuilder setTemplateType(String templateType) {
        this.templateType = templateType;
        return this;
    }

    public SendMailRequestBuilder setTemplateParams(Map<String, Object> templateParams) {
        this.templateParams = templateParams;
        return this;
    }

    public SendMailRequestBuilder setAttachmentsPaths(List<FilePath> attachmentsPaths) {
        this.attachmentsPaths = attachmentsPaths;
        return this;
    }

    public SendMailRequestBuilder setAttachmentsBase64Strings(List<FileString> attachmentsBase64Strings) {
        this.attachmentsBase64Strings = attachmentsBase64Strings;
        return this;
    }

    public SendMailRequest build() {
        return new SendMailRequest(to, from, subject, cc, bcc, templateType, templateParams, attachmentsPaths, attachmentsBase64Strings);
    }
}