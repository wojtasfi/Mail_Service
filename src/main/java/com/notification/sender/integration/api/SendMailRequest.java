package com.notification.sender.integration.api;

import com.notification.sender.domain.dto.MailDto;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SendMailRequest {
    private String to;
    private String from;
    private String subject;

    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();

    @ApiParam("Indicates in which language template should be. Default value can be set in application properties.s")
    private Locale locale;

    @ApiParam("Type of template which will be used. Has to be consistent with templateParams. This name will be also used as mail`s type.")
    private String templateType;

    @ApiParam("Name and values for the parameters used on mail template. This is the content of the email.")
    private Map<String, Object> templateParams = new HashMap<>();

    @ApiParam("List of urls to files to be send as attachments.")
    private List<FilePath> attachmentsPaths = new ArrayList<>();

    @ApiParam("List of files to be send as attachments encoded as base64 string.")
    private List<FileString> attachmentsBase64Strings = new ArrayList<>();

    public MailDto toDto() {
        return new MailDto(to, from, subject, locale, cc, bcc, templateType, templateParams, attachmentsPaths, attachmentsBase64Strings);
    }

}

