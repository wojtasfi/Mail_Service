package com.notification.sender.domain.dto;

import com.notification.sender.api.FilePath;
import com.notification.sender.api.FileString;
import lombok.Value;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Value
public class MailDto {
    private String to;
    private String from;
    private String subject;
    private Locale locale;
    private List<String> cc;
    private List<String> bcc;
    private String templateType;
    private Map<String, Object> templateParams;
    private List<FilePath> attachmentsPaths;
    private List<FileString> attachmentsBase64Strings;

}
