package com.notification.integration.msg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.sender.integration.msg.ResendErrorMessage;
import com.notification.sender.integration.msg.SendMailMessage;
import com.notification.sender.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

import static com.notification.integration.msg.MailMessageSender.resendTopic;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailMessageListener {

    private final ObjectMapper mapper;
    private final MailService mailService;
    private final MailMessageSender sender;


    @KafkaListener(topics = "sendMail")
    public void handleSendMailMessage(String message) {
        log.info("Received <" + message + ">");

        SendMailMessage sendMailMessage = convertMessage(message);

        try {
            mailService.sendMail(sendMailMessage.toDto());
        } catch (MessagingException e) {
            sender.putMessageOnResendTopic(sendMailMessage);
        }

    }

    @KafkaListener(topics = resendTopic)
    public void handleSendMailErrorMessage(String message) {
        log.info("Received <" + message + ">");

        ResendErrorMessage resendErrorMessage = convertMessageToResendErrorMessage(message);

        if (validateRetries(resendErrorMessage)) return;

        resendErrorMessage.lowerRetries();
        try {
            mailService.sendMail(resendErrorMessage.toMailDto());
        } catch (MessagingException e) {
            sender.putMessageOnResendTopic(resendErrorMessage);
        }
    }

    private boolean validateRetries(ResendErrorMessage resendErrorMessage) {
        int retries = resendErrorMessage.getRetriesLeft();
        retries = retries - 1;
        return retries < 1;
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

    private ResendErrorMessage convertMessageToResendErrorMessage(String message) {
        ResendErrorMessage resendErrorMessage = new ResendErrorMessage();
        try {
            resendErrorMessage = mapper.readValue(message, ResendErrorMessage.class);
        } catch (IOException e) {
            log.error("Could not read json value", e);
        }
        return resendErrorMessage;
    }
}
