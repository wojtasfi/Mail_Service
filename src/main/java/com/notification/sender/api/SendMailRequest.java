package com.notification.sender.api;

import com.notification.sender.domain.dto.MailDto;
import io.swagger.annotations.ApiParam;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Value
public class SendMailRequest {
    @NotNull
    private String to;

    @NotNull
    private String from;

    @NotNull
    private String subject;

    private List<String> cc;
    private List<String> bcc;

    @NotNull
    @ApiParam("Type of template which will be used. Has to be consistent with templateParams. This name will be also used as mail`s type.")
    private String templateType;

    @NotNull
    @ApiParam("Name and values for the parameters used on mail template. This is the content of the email.")
    private Map<String, Object> templateParams;

    @ApiParam("List of urls to files to be send as attachments.")
    private List<FilePath> attachmentsPaths;

    @ApiParam("List of files to be send as attachments encoded as base64 string.")
    private List<FileString> attachmentsBase64Strings;


    MailDto toDto() {
        return new MailDto(to, from, subject, cc, bcc, templateType, templateParams, attachmentsPaths, attachmentsBase64Strings);
    }

}
