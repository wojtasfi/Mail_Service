package com.notification.sender.integration.api;

import com.notification.integration.api.MailController;
import com.notification.integration.msg.MailMessageSender;
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
    private final SendMailRequestValidator validator;
    private final MailMessageSender sender;

    @Override
    public ResponseEntity<String> sendMail(@RequestBody SendMailRequest request, Errors errors) {
        validator.validate(request, errors);

        if (errors.hasErrors()) {
            String errorsString = retrieveErrorsAsString(errors);
            return ResponseEntity.badRequest().body(format("Following fields have incorrect values: %s", errorsString));
        }

        try {
            mailService.sendMail(request.toDto());
        } catch (Exception e) {
            //todo should we put it to retry with API call also?
            //sender.putMessageOnResendTopic(request.toDto());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(format("An error occurred please contact administrator: %s", e));
        }

        return ResponseEntity.ok().build();
    }

    private String retrieveErrorsAsString(Errors errors) {
        StringBuilder errorsString = new StringBuilder();
        errors.getAllErrors()
                .forEach(err -> errorsString.append(err.toString()).append(System.getProperty("line.separator")));
        return errorsString.toString();
    }
}
