package com.notification.sender.api;

import com.notification.sender.domain.dto.MailDto;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SendMailRequest {
    private String to;
    private String from;
    private String subject;

    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();

    @ApiParam("Type of template which will be used. Has to be consistent with templateParams. This name will be also used as mail`s type.")
    private String templateType;

    @ApiParam("Name and values for the parameters used on mail template. This is the content of the email.")
    private Map<String, Object> templateParams = new HashMap<>();

    @ApiParam("List of urls to files to be send as attachments.")
    private List<FilePath> attachmentsPaths = new ArrayList<>();

    @ApiParam("List of files to be send as attachments encoded as base64 string.")
    private List<FileString> attachmentsBase64Strings = new ArrayList<>();

    public MailDto toDto() {
        return new MailDto(to, from, subject, cc, bcc, templateType, templateParams, attachmentsPaths, attachmentsBase64Strings);
    }

}

