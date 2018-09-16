package com.notification.sender.integration.api;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class SendMailRequestValidator implements Validator {


    @Value("${attachment.max.size.mb}")
    private int attachmentMaxSize;

    private final int BYTE_TO_MB_RATION = 1000000;

    @Override
    public boolean supports(Class<?> aClass) {
        return SendMailRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SendMailRequest request = (SendMailRequest) target;
        validateRequiredFields(errors, request);
        //todo how to validate files urls?
        validateBase64Attachments(errors, request);
        validateEmailAddresses(errors, request);
    }

    private void validateRequiredFields(Errors errors, SendMailRequest request) {

        if (Strings.isNullOrEmpty(request.getTo())) {
            errors.rejectValue("to", "", "Sender must not be null");
        }
        if (Strings.isNullOrEmpty(request.getFrom())) {
            errors.rejectValue("from", "", "Recipient must not be null");
        }
        if (Strings.isNullOrEmpty(request.getSubject())) {
            errors.rejectValue("subject", "", "Subject must not be null");
        }
        if (Strings.isNullOrEmpty(request.getTemplateType())) {
            errors.rejectValue("templateType", "", "Template type must not be null");
        }
    }

    private void validateEmailAddresses(Errors errors, SendMailRequest request) {

        EmailValidator emailValidator = EmailValidator.getInstance();

        if (!emailValidator.isValid(request.getTo())) {
            errors.rejectValue("to", "",
                    format("Not valid email address: %s", request.getTo()));
        }
        if (!emailValidator.isValid(request.getFrom())) {
            errors.rejectValue("from", "",
                    format("Not valid email address: %s", request.getFrom()));
        }

        //todo extract to common consumer?
        request.getCc().forEach(mail -> {
            if (!emailValidator.isValid(mail)) {
                errors.rejectValue("cc", "",
                        format("Not valid email address: %s", mail));
            }
        });

        request.getBcc().forEach(mail -> {
            if (!emailValidator.isValid(mail)) {
                errors.rejectValue("bcc", "",
                        format("Not valid email address: %s", mail));
            }
        });
    }

    private void validateBase64Attachments(Errors errors, SendMailRequest request) {
        List<String> toLargeFiles = retrieveToLargeFiles(request);
        if (!toLargeFiles.isEmpty()) {
            val fileNames = retrieveFileNames(toLargeFiles);
            errors.rejectValue("attachmentsBase64Strings", "",
                    format("Following files are exceeding attachment size limit: %s", fileNames));
        }
    }

    private List<String> retrieveToLargeFiles(SendMailRequest request) {
        return request.getAttachmentsBase64Strings().stream()
                .filter(fileInfo -> getSizeOfBase64StringFileInMb(fileInfo.getFileString()) > attachmentMaxSize)
                .map(FileString::getFileName)
                .collect(Collectors.toList());
    }

    private String retrieveFileNames(List<String> toLargeFiles) {
        StringBuilder fileNames = new StringBuilder();
        toLargeFiles.forEach(s -> fileNames.append(s).append(" "));
        return fileNames.toString();
    }

    private double getSizeOfBase64StringFileInMb(String file) {
        return (4 * Math.ceil(file.length() / 3)) / BYTE_TO_MB_RATION;
    }
}
