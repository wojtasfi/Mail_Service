package com.notification.sender.integration.msg;

import com.notification.sender.domain.dto.MailDto;
import com.notification.sender.integration.api.FilePath;
import com.notification.sender.integration.api.FileString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResendErrorMessage {
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
    private int retriesLeft;

    public MailDto toMailDto() {
        return new MailDto(to, from, subject, locale, cc, bcc, templateType, templateParams, attachmentsPaths, attachmentsBase64Strings);
    }

    public static ResendErrorMessage from(SendMailMessage message, int retries) {
        return new ResendErrorMessage(
                message.getTo(), message.getFrom(), message.getSubject(), message.getCc(),
                message.getBcc(),
                message.getLocale(),
                message.getTemplateType(),
                message.getTemplateParams(),
                message.getAttachmentsPaths(),
                message.getAttachmentsBase64Strings(),
                retries
        );
    }

    public static ResendErrorMessage from(MailDto message, int retries) {
        return new ResendErrorMessage(
                message.getTo(), message.getFrom(), message.getSubject(), message.getCc(),
                message.getBcc(),
                message.getLocale(),
                message.getTemplateType(),
                message.getTemplateParams(),
                message.getAttachmentsPaths(),
                message.getAttachmentsBase64Strings(),
                retries
        );
    }

    public void lowerRetries() {
        this.retriesLeft = retriesLeft - 1;
    }
}
