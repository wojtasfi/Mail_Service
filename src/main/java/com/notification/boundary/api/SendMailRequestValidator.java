package com.notification.boundary.api;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
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
        //todo how to validate files urls?
        validateBase64Attachments(errors, request);

    }

    private void validateBase64Attachments(Errors errors, SendMailRequest request) {
        List<String> toLargeFiles = retrieveToLargeFiles(request);
        if (!toLargeFiles.isEmpty()) {
            val fileNames = retrieveFileNames(toLargeFiles);
            errors.rejectValue("attachmentsBase64Strings", "",
                    format("Following file are exceeding attachment size limit: %s", fileNames));
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
