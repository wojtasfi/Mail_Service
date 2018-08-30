package com.notification.boundary.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
public class MailControllerImpl implements MailController {

    @Override
    public ResponseEntity<String> sendMail(@RequestBody SendMailRequest request, Errors errors) {
        return null;
    }
}
