package com.notification.boundary.api;

import com.notification.sender.api.SendMailRequest;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Mail")
@RestController
@RequestMapping(MailController.BASE_PATH)
public interface MailController {

    String BASE_PATH = "mail";

    @PostMapping
    ResponseEntity<String> sendMail(@RequestBody SendMailRequest request, Errors errors);
}
