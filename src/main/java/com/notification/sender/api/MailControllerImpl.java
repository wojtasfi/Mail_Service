package com.notification.sender.api;

import com.notification.boundary.api.MailController;
import com.notification.sender.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MailControllerImpl implements MailController {

    private final MailService mailService;

    @Override
    public ResponseEntity<String> sendMail(@RequestBody SendMailRequest request, Errors errors) {
        try {
            mailService.sendMail(request.toDto());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(format("An error occured please contact administrator: %s", e));
        }

        return ResponseEntity.ok().build();
    }
}
