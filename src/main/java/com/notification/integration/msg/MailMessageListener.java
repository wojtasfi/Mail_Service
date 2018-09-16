package com.notification.integration.msg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.sender.integration.msg.SendMailMessage;
import com.notification.sender.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailMessageListener {

    private final ObjectMapper mapper;
    private final MailService mailService;

    @KafkaListener(topics = "sendMail")
    public void handleSendMailMessage(String message) {
        log.info("Received <" + message + ">");

        SendMailMessage sendMailMessage = convertMessage(message);

        try {
            mailService.sendMail(sendMailMessage.toDto());
        } catch (MessagingException e) {
            //todo put to error topics
            e.printStackTrace();
        }

    }

    private SendMailMessage convertMessage(String message) {
        SendMailMessage sendMailMessage = new SendMailMessage();
        try {
            sendMailMessage = mapper.readValue(message, SendMailMessage.class);
        } catch (IOException e) {
            log.error("Could not read json value", e);
        }
        return sendMailMessage;
    }

    @KafkaListener(topics = "sendMailError")
    public void handleSendMailErrorMessage() {

    }
}
